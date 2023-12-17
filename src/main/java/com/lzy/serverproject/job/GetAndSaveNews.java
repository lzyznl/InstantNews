package com.lzy.serverproject.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzy.serverproject.Service.DayNewsNumService;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.mapper.DayNewsNumMapper;
import com.lzy.serverproject.model.entity.DayNewsNum;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.utils.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 获取新闻并且存储到文本当中
 * @author lzy
 */
@Component
public class GetAndSaveNews {

    List<News> preJapaneseNewsList = new ArrayList<>();
    List<News> preChineseNewsList = new ArrayList<>();

    @Resource
    private DayNewsNumService dayNewsNumService;

    @Resource
    private DayNewsNumMapper dayNewsNumMapper;


    @Scheduled(fixedRate = 7 * 60 * 1000)
    public void task() {
        System.out.println("开始执行定时任务");
        Map<String, String> urlMap = UrlMapUtil.urlMap();
        // 从线程池中获取一个线程，进行多线程工作
        ExecutorService executorService = NewsThreadProcessor.getExecutorService();
        for (Entry<String, String> entry : urlMap.entrySet()) {
            String newsType = entry.getKey();
            String newsUrl = entry.getValue();
            System.out.println("开始爬取新闻" + newsType);
            System.out.println("爬取新闻地址" + newsUrl);
            // 提交任务
            Future<?> japaneseFuture = executorService.submit(() -> {
                System.out.println("开始爬取日文新闻");
                List<News> japaneseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getJapaneseNewsList(newsUrl), false);
                if (preJapaneseNewsList.size() == 0) {
                    //存储list中的内容
                    SaveNewsListUtil.save(japaneseNewsContentList, false, newsType);
                    //向数据库中进行存储
                    Boolean fact = insertIntoDataBase(newsType, japaneseNewsContentList.size());
                    if(!fact){
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储新闻数量到数据库异常");
                    }
                } else {
                    List<News> SaveJapaneseNewsList = new ArrayList<>();
                    //比较之后创建新list存储内容
                    for (News news : japaneseNewsContentList) {
                        for (News value : preJapaneseNewsList) {
                            if (!news.getNewsTitle().equals(value.getNewsTitle())) {
                                SaveJapaneseNewsList.add(news);
                            }
                        }
                    }
                    preJapaneseNewsList.clear();
                    preJapaneseNewsList.addAll(japaneseNewsContentList);
                    //存储list中的内容
                    SaveNewsListUtil.save(SaveJapaneseNewsList, false, newsType);
                    //向数据库中进行存储
                    Boolean fact = insertIntoDataBase(newsType, SaveJapaneseNewsList.size());
                    if(!fact){
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储新闻数量到数据库异常");
                    }
                }
                System.out.println("爬取并存储日文新闻结束");
            });
            Future<?> chineseFuture = executorService.submit(() -> {
                System.out.println("开始爬取中文新闻");
                List<News> chineseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getChineseNewsList(newsUrl), true);
                if (preChineseNewsList.size() == 0) {
                    //直接存储内容
                    SaveNewsListUtil.save(chineseNewsContentList, true, newsType);
                } else {
                    List<News> SaveChineseNewsList = new ArrayList<>();
                    //比较之后存储内容
                    for (News news : chineseNewsContentList) {
                        for (News value : preChineseNewsList) {
                            if (!news.getNewsTitle().equals(value.getNewsTitle())) {
                                SaveChineseNewsList.add(news);
                            }
                        }
                    }
                    preChineseNewsList.clear();
                    preJapaneseNewsList.addAll(chineseNewsContentList);
                    //存储list中的内容
                    SaveNewsListUtil.save(SaveChineseNewsList, true, newsType);
                }
                System.out.println("爬取并存储中文新闻结束");
            });
            try {
                japaneseFuture.get();
                chineseFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("执行完成");
        }
        System.out.println("本次定时任务所有任务执行完毕");
        // 关闭线程池
        executorService.shutdown();
    }

    public Boolean insertIntoDataBase(String newsType, int listSize) {
        //向数据库中进行存储
        String systemTime = SaveNewsListUtil.getSystemTime();
        QueryWrapper<DayNewsNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dayTime", systemTime);
        DayNewsNum dayNewsNum = dayNewsNumMapper.selectOne(queryWrapper);
        if (dayNewsNum == null) {
            DayNewsNum insertDayNewsNum = new DayNewsNum();
            insertDayNewsNum.setDayTime(systemTime);
            insertDayNewsNum = setDifferentNewsTypeNum(insertDayNewsNum, null, newsType, listSize);
            //向数据库中进行存储
            int insert = dayNewsNumMapper.insert(insertDayNewsNum);
            return insert>0;
        } else {
            dayNewsNum = setDifferentNewsTypeNum(dayNewsNum, dayNewsNum, newsType, listSize);
            int update = dayNewsNumMapper.updateById(dayNewsNum);
            return update>0;
        }
    }

    public DayNewsNum setDifferentNewsTypeNum(DayNewsNum insertDayNewsNum, DayNewsNum queryedDayNewsNum, String newsType, int listSize) {
        switch (newsType) {
            case NewsFileConstant.NewsType_LIVE:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setLiveNum(listSize);
                } else {
                    insertDayNewsNum.setLiveNum(queryedDayNewsNum.getLiveNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_ECONOMY:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setEconomyNum(listSize);
                } else {
                    insertDayNewsNum.setEconomyNum(queryedDayNewsNum.getEconomyNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_INTERIOR:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setInteriorNum(listSize);
                } else {
                    insertDayNewsNum.setInteriorNum(queryedDayNewsNum.getInteriorNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_INTERNATIONAL:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setInteriorNum(listSize);
                } else {
                    insertDayNewsNum.setInterNationalNum(queryedDayNewsNum.getInterNationalNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_IT:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setITNum(listSize);
                } else {
                    insertDayNewsNum.setITNum(queryedDayNewsNum.getITNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_PHYSICAL:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setPhysicalNum(listSize);
                } else {
                    insertDayNewsNum.setPhysicalNum(queryedDayNewsNum.getPhysicalNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_RECREATION:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setRecreationNum(listSize);
                } else {
                    insertDayNewsNum.setRecreationNum(queryedDayNewsNum.getRecreationNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_SCIENCE:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setScienceNum(listSize);
                } else {
                    insertDayNewsNum.setScienceNum(queryedDayNewsNum.getScienceNum() + listSize);
                }
                break;
            case NewsFileConstant.NewsType_GEO:
                if (queryedDayNewsNum == null) {
                    //说明是第一次插入
                    insertDayNewsNum.setGeoNum(listSize);
                } else {
                    insertDayNewsNum.setGeoNum(queryedDayNewsNum.getGeoNum() + listSize);
                }
                break;
            default:
        }
        if (queryedDayNewsNum == null) {
            //说明是第一次插入
            insertDayNewsNum.setAllNum((long) listSize);
        } else {
            insertDayNewsNum.setAllNum(queryedDayNewsNum.getAllNum() + (long) listSize);
        }
        return insertDayNewsNum;
    }
}
