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
import com.lzy.serverproject.utils.common.CommonUtils;
import com.lzy.serverproject.utils.news.GetNewsContentUtil;
import com.lzy.serverproject.utils.news.GetNewsListUtil;
import com.lzy.serverproject.utils.news.NewsThreadProcessor;
import com.lzy.serverproject.utils.news.SaveNewsListUtil;
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
    UrlMapUtil<News> urlMapUtil = new UrlMapUtil<News>();
    Map<String,List<News>> preJapaneseNewsListMap = urlMapUtil.listMap();
    Map<String,List<News>> preChineseNewsListMap = urlMapUtil.listMap();

    @Resource
    private DayNewsNumService dayNewsNumService;

    @Resource
    private DayNewsNumMapper dayNewsNumMapper;


//    @Scheduled(fixedRate = 7 * 60 * 1000)
    public void task() {
        System.out.println("开始执行定时任务");
        Map<String, String> urlMap = UrlMapUtil.urlMap();
        // 从线程池中获取一个线程，进行多线程工作
        ExecutorService executorService = NewsThreadProcessor.getExecutorService();
        for (Entry<String, String> entry : urlMap.entrySet()) {
            String newsType = entry.getKey();
            String newsUrl = entry.getValue();
            List<News> preJapaneseNewsList = preJapaneseNewsListMap.get(newsType);
            List<News> preChineseNewsList = preChineseNewsListMap.get(newsType);
            System.out.println("开始爬取新闻" + newsType);
            System.out.println("爬取新闻地址" + newsUrl);
            System.out.println("开始爬取中文以及日文新闻");

            //爬取日文新闻
            List<News> japaneseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getJapaneseNewsList(newsUrl), false);
            if (preJapaneseNewsList.size() == 0) {
                //对新闻进行编号，此处的新闻编号肯定是从0开始
                japaneseNewsContentList = setListNewsId(japaneseNewsContentList,0);
                //存储list中的内容
                SaveNewsListUtil.save(japaneseNewsContentList, false, newsType);
                //向数据库中进行存储
                Boolean fact = insertIntoDataBase(newsType, japaneseNewsContentList.size());
                if(!fact){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储新闻数量到数据库异常");
                }
                preJapaneseNewsList.addAll(japaneseNewsContentList);
            } else {
                List<News> SaveJapaneseNewsList = new ArrayList<>();
                //比较之后创建新list存储内容
                for(int i=0;i<japaneseNewsContentList.size();++i){
                    News news = japaneseNewsContentList.get(i);
                    int flag=0;
                    for(int j=0;j<preChineseNewsList.size();++j){
                        News value = preChineseNewsList.get(j);
                        if (news.getNewsTitle().equals(value.getNewsTitle())) {
                            flag=1;
                        }
                    }
                    if(flag==1){
                        SaveJapaneseNewsList.add(news);
                    }
                }
                preJapaneseNewsList.clear();
                preJapaneseNewsList.addAll(japaneseNewsContentList);
                System.out.println("此次存储"+newsType+"新闻"+SaveJapaneseNewsList.size()+"条");
                //给新闻进行编号
                String systemTime = CommonUtils.getSystemTime();
                QueryWrapper<DayNewsNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("dayTime",systemTime);
                DayNewsNum dayNewsNum = dayNewsNumMapper.selectOne(queryWrapper);
                Integer startId = getExplicitNewsTypeStartId(dayNewsNum, newsType);
                if(startId==-1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"新闻编号设置错误");
                }
                SaveJapaneseNewsList = setListNewsId(SaveJapaneseNewsList,startId);
                //存储list中的内容
                SaveNewsListUtil.save(SaveJapaneseNewsList, false, newsType);
                //向数据库中进行存储
                Boolean fact = insertIntoDataBase(newsType, SaveJapaneseNewsList.size());
                if(!fact){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储新闻数量到数据库异常");
                }
            }
            System.out.println("爬取并存储中文以及日文新闻结束");
        }
        System.out.println("一次定时任务现在全部结束");
    }
    
    public List<News> setListNewsId(List<News> newsList,int start){
        for(int i=0;i<newsList.size();++i){
            News news = newsList.get(i);
            news.setNewsId(start+i);
        }
        return newsList;
    }
    
    public Integer getExplicitNewsTypeStartId(DayNewsNum dayNewsNum,String newsType){
        int startId = -1;
        switch (newsType){
            case NewsFileConstant.NewsType_LIVE:
                startId=dayNewsNum.getLiveNum();
            case NewsFileConstant.NewsType_INTERIOR:
                startId=dayNewsNum.getInteriorNum();
            case NewsFileConstant.NewsType_GEO:
                startId= dayNewsNum.getGeoNum();
            case NewsFileConstant.NewsType_INTERNATIONAL:
                startId=dayNewsNum.getInterNationalNum();
            case NewsFileConstant.NewsType_SCIENCE:
                startId=dayNewsNum.getScienceNum();
            case NewsFileConstant.NewsType_ECONOMY:
                startId=dayNewsNum.getEconomyNum();
            case NewsFileConstant.NewsType_PHYSICAL:
                startId=dayNewsNum.getPhysicalNum();
            case NewsFileConstant.NewsType_RECREATION:
                startId = dayNewsNum.getRecreationNum();
            case NewsFileConstant.NewsType_IT:
                startId = dayNewsNum.getITNum();
        }
        return startId;
    }

    public Boolean insertIntoDataBase(String newsType, int listSize) {
        //向数据库中进行存储
        String systemTime = CommonUtils.getSystemTime();
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
