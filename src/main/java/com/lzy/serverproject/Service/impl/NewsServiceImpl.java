package com.lzy.serverproject.Service.impl;

import cn.hutool.core.io.FileUtil;
import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.mapper.DayNewsNumMapper;
import com.lzy.serverproject.model.entity.DayNewsNum;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.model.vo.*;
import com.lzy.serverproject.utils.UrlMapUtil;
import com.lzy.serverproject.utils.common.CommonUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.File;
import java.util.*;


@Service
public class NewsServiceImpl implements NewsService {


    @Resource
    private DayNewsNumMapper dayNewsNumMapper;


    /**
     * 获取新闻
     * @param newsType
     * @param newsLange
     * @param newsTime
     * @param initSize
     * @param addSize
     * @param currentNewsNum
     * @return
     */
    @Override
    public getNewsVo getNews(int newsType, int newsLange, String newsTime, int initSize, int addSize, int currentNewsNum) {
        List<News> newsList = new ArrayList<>();
        newsTypeEnum newsEnum = newsTypeEnum.getEnumByValue(newsType);
        getNewsVo getNewsVo = new getNewsVo();
        if(newsEnum==null){
            getNewsVo.setNewsList(newsList);
            getNewsVo.setNewsSize(0);
            return getNewsVo;
        }
        String type = newsEnum.getText();
        boolean isChinese = newsLange != 0;
        String path = "";
        //构造新闻文件路径
        if(isChinese){
            path = getPath(type, newsTime, isChinese);
        }else{
            path = getPath(type, newsTime, false);
        }
        if(path.equals("")){
            getNewsVo.setNewsList(newsList);
            getNewsVo.setNewsSize(0);
            return getNewsVo;
        }
        //读取该文件中的所有内容
        List<News> newsLists = CommonUtils.readJsonFile(path);
        //处理新闻数据，不要全部返回
        for (News news:newsLists){
            news.setNewsContent(news.getNewsContent().substring(0, NewsConstant.NEWS_CONTENT_LENGTH)+"......");
        }
        //处理读取到的数据然后进行返回
        List<News> subList = null;
        if(initSize!=0){
            //初次加载
            subList = getSubList(newsLists, 0, initSize);
        }else if(addSize!=0&currentNewsNum!=0){
            //动态加载
            subList = getSubList(newsLists,(currentNewsNum-1),addSize);
        }
        getNewsVo.setNewsList(subList);
        getNewsVo.setNewsSize(subList.size());
        return getNewsVo;
    }


    /**
     * 获取新闻发布数量的数据
     * @return
     */
    @Override
    public NewsDataVo getNewsPubData() {
        UrlMapUtil<DayTimeVo> dayTimeVoUrlMapUtil = new UrlMapUtil<>();
        UrlMapUtil<WeekTimeVo> weekTimeVoUrlMapUtil = new UrlMapUtil<>();
        UrlMapUtil<MonthTimeVo> monthTimeVoUrlMapUtil = new UrlMapUtil<>();
        List<DayNewsNum> dayNewsNums = dayNewsNumMapper.selectList(null);
        Map<String,List<DayTimeVo>> dayMap = dayTimeVoUrlMapUtil.listMap();
        Map<String, List<WeekTimeVo>> weekMap = weekTimeVoUrlMapUtil.listMap();
        Map<String, List<MonthTimeVo>> monthMap = monthTimeVoUrlMapUtil.listMap();
        String preTime = dayNewsNums.get(0).getDayTime().substring(0,7);
        //[live,economy,physical,international,intrior,science,it,geo,recreation]
        for(int i=0;i<dayNewsNums.size();++i){
            DayNewsNum dayNewsNum = dayNewsNums.get(i);
            DayTimeVo dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getEconomyNum());
            dayMap.get(NewsFileConstant.NewsType_ECONOMY).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getITNum());
            dayMap.get(NewsFileConstant.NewsType_IT).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getGeoNum());
            dayMap.get(NewsFileConstant.NewsType_GEO).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getInteriorNum());
            dayMap.get(NewsFileConstant.NewsType_INTERIOR).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getLiveNum());
            dayMap.get(NewsFileConstant.NewsType_LIVE).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getInterNationalNum());
            dayMap.get(NewsFileConstant.NewsType_INTERNATIONAL).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getRecreationNum());
            dayMap.get(NewsFileConstant.NewsType_RECREATION).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getScienceNum());
            dayMap.get(NewsFileConstant.NewsType_SCIENCE).add(dayTimeVo);
            dayTimeVo = new DayTimeVo();
            dayTimeVo.setDayTime(dayNewsNum.getDayTime());
            dayTimeVo.setValue(dayNewsNum.getPhysicalNum());
            dayMap.get(NewsFileConstant.NewsType_PHYSICAL).add(dayTimeVo);
        }
        NewsDataVo newsDataVo = new NewsDataVo();
        newsDataVo.setDayMap(dayMap);
        weekMap = processWeekData(dayMap);
        newsDataVo.setWeekMap(weekMap);
        monthMap = processMonthData(dayMap);
        newsDataVo.setMonthMap(monthMap);
        return newsDataVo;
    }

    @Override
    public ExplicitNewsContentVo getExplicitNewsContent(Integer newsType, String newsTime, Integer newsId) {
        ExplicitNewsContentVo explicitNewsContentVo = new ExplicitNewsContentVo();
        newsTypeEnum enumByValue = newsTypeEnum.getEnumByValue(newsType);
        if(enumByValue==null){
            return explicitNewsContentVo;
        }
        String type = enumByValue.getText();
        String path = System.getProperty("user.dir");
        String dirPath = path+File.separator+NewsFileConstant.TotalNewsFileDir+File.separator
                +NewsFileConstant.JapaneseNewsFileDir+File.separator+type+File.separator;
        if(newsTime.equals("")){
            //默认当前时间
            String systemTime = CommonUtils.getSystemTime();
            String filePath = dirPath+(systemTime+".json");
            List<News> newsList = CommonUtils.readJsonFile(filePath);
            News news = newsList.get(newsId);
            explicitNewsContentVo.setNewsContent(news.getNewsContent());
            explicitNewsContentVo.setNewsTitle(news.getNewsTitle());
            explicitNewsContentVo.setNewsTime(news.getNewsTime());
        }else{
            //根据给定时间
            String filePath = dirPath+(newsTime+".json");
            List<News> newsList = CommonUtils.readJsonFile(filePath);
            News news = newsList.get(newsId);
            explicitNewsContentVo.setNewsContent(news.getNewsContent());
            explicitNewsContentVo.setNewsTitle(news.getNewsTitle());
            explicitNewsContentVo.setNewsTime(news.getNewsTime());
        }
        return explicitNewsContentVo;
    }


    /**
     * 根据每天每种新闻的数据，归类出每月的数据
     * @param dayMap
     * @return
     */
    public Map<String, List<WeekTimeVo>> processWeekData(Map<String,List<DayTimeVo>> dayMap){
        UrlMapUtil<WeekTimeVo> urlMapUtil = new UrlMapUtil<>();
        Map<String,List<WeekTimeVo>> weekMap = urlMapUtil.listMap();
        for(Map.Entry<String,List<DayTimeVo>> entry:dayMap.entrySet()){
            String type = entry.getKey();
            List<DayTimeVo> dayList = entry.getValue();
            int count=0;
            String preWeekTime = "";
            String lasWeekTime = "";
            int weekTotalSize = 0;
            for(int i=0;i<dayList.size();++i){
                count++;
                DayTimeVo dayTimeVo = dayList.get(i);
                weekTotalSize+=dayTimeVo.getValue();
                if(count==1){
                    preWeekTime = dayTimeVo.getDayTime();
                }else if(count==7){
                    lasWeekTime = dayTimeVo.getDayTime();
                    WeekTimeVo weekTimeVo = new WeekTimeVo();
                    weekTimeVo.setWeekTime(preWeekTime+"-"+lasWeekTime);
                    weekTimeVo.setValue(weekTotalSize);
                    weekMap.get(type).add(weekTimeVo);
                    count=0;
                    weekTotalSize=0;
                }else if(i==dayList.size()-1){
                    lasWeekTime = dayTimeVo.getDayTime();
                    WeekTimeVo weekTimeVo = new WeekTimeVo();
                    weekTimeVo.setWeekTime(preWeekTime+"-"+lasWeekTime);
                    weekTimeVo.setValue(weekTotalSize);
                    weekMap.get(type).add(weekTimeVo);
                    count=0;
                    weekTotalSize=0;
                }
            }
        }
        return weekMap;
    }

    public Map<String,List<MonthTimeVo>> processMonthData(Map<String,List<DayTimeVo>> dayMap){
        UrlMapUtil<MonthTimeVo> urlMapUtil = new UrlMapUtil<>();
        Map<String, List<MonthTimeVo>> monthMap = urlMapUtil.listMap();
        for(Map.Entry<String,List<DayTimeVo>> entry:dayMap.entrySet()){
            String type = entry.getKey();
            List<DayTimeVo> dayList = entry.getValue();
            String preTime = dayList.get(0).getDayTime().substring(0,7);
            int monthTotalSize = 0;
            for(int i=0;i<dayList.size();++i){
                DayTimeVo dayTimeVo = dayList.get(i);
                monthTotalSize+=dayTimeVo.getValue();
                if(!dayTimeVo.getDayTime().substring(0,7).equals(preTime)){
                    MonthTimeVo monthTimeVo = new MonthTimeVo();
                    monthTimeVo.setMonthTime(preTime);
                    monthTimeVo.setValue((long) monthTotalSize);
                    monthMap.get(type).add(monthTimeVo);
                    monthTotalSize=0;
                    preTime=dayTimeVo.getDayTime().substring(0,7);
                }else if(i==dayList.size()-1){
                    MonthTimeVo monthTimeVo = new MonthTimeVo();
                    monthTimeVo.setMonthTime(preTime);
                    monthTimeVo.setValue((long) monthTotalSize);
                    monthMap.get(type).add(monthTimeVo);
                    monthTotalSize=0;
                }
            }
        }
        return monthMap;
    }



    public static List<News> getSubList(List<News> originalList, int start,int num) {
        // 检查开始位置是否有效
        if (start < 0 || start >= originalList.size()) {
            throw new IllegalArgumentException("Invalid start position");
        }

        // 计算结束位置
        int end = Math.min(start + num, originalList.size());

        // 使用subList方法获取子列表
        List<News> subList = originalList.subList(start, end);

        // 创建新的ArrayList，将子列表的元素添加到新列表中
        return new ArrayList<>(subList);
    }


    public String getPath(String type,String newsTime,boolean isChinese){
        String projectFilePath = System.getProperty("user.dir");
        String newsFilePath = "";
        String finalPath = "";
        String systemTime = CommonUtils.getSystemTime();
        if(isChinese){
            newsFilePath=projectFilePath+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator+NewsFileConstant.ChineseNewsFileDir
                    +File.separator+type;
        }else{
            newsFilePath=projectFilePath+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator+NewsFileConstant.JapaneseNewsFileDir
                    +File.separator+type;
        }
        if(!newsTime.equals("")){
            if(!CommonUtils.isValidDateFormat(newsTime)){
                return "";
            }
            finalPath = newsFilePath+File.separator+newsTime+".json";
            if(!FileUtil.exist(finalPath)){
                return "";
            }
        }else{
            finalPath = newsFilePath+File.separator+(systemTime+".json");
        }
        return finalPath;
    }
}
