package com.lzy.serverproject.Service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.mapper.DayNewsNumMapper;
import com.lzy.serverproject.model.dto.SearchNewsRequest;
import com.lzy.serverproject.model.entity.DayNewsNum;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.model.vo.*;
import com.lzy.serverproject.utils.UrlMapUtil;
import com.lzy.serverproject.utils.common.CommonUtils;
import com.lzy.serverproject.utils.translate.TranslateUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        List<NewsVo> newsList = new ArrayList<>();
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
        //处理读取到的数据
        List<News> subList = null;
        if(initSize!=0){
            //初次加载
            subList = getSubList(newsLists, 0, initSize);
        }else if(addSize!=0&currentNewsNum!=0){
            //动态加载
            subList = getSubList(newsLists,currentNewsNum,addSize);
        }
        if(subList!=null&&subList.size()==0){
            getNewsVo.setNewsList(new ArrayList<>());
            getNewsVo.setNewsSize(0);
            return getNewsVo;
        }
        List<NewsVo> newsVoList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        //处理新闻数据，不要全部返回
        for(int i=0;i<subList.size();++i){
            NewsVo newsVo = new NewsVo();
            News news = subList.get(i);
            String title = news.getNewsTitle();
            String content = news.getNewsContent();
            if(content.length()>NewsConstant.NEWS_CONTENT_LENGTH){
                news.setNewsContent(news.getNewsContent().substring(0,NewsConstant.NEWS_CONTENT_LENGTH));
            }
            newsVo.setNewsId(news.getNewsId());
            newsVo.setNewsTitle(news.getNewsTitle());
            newsVo.setNewsContent(news.getNewsContent());
            newsVo.setNewsTime(news.getNewsTime());
            newsVo.setNewsLink(news.getNewsLink());
            newsVo.setNewsImage(news.getNewsImage());
            newsVoList.add(newsVo);
            if(i!=subList.size()-1){
                stringBuilder.append(title).append("\n").append(news.getNewsContent()).append("\n");
            }else{
                stringBuilder.append(title).append("\n").append(news.getNewsContent());
            }
        }
        String translated = stringBuilder.toString();
        List<String> translatedList = TranslateUtil.batchTranslate(translated);
        for(int i=0;i<newsVoList.size();i++){
            NewsVo newsVo = newsVoList.get(i);
            newsVo.setNewsChineseTitle(translatedList.get(2*i));
            newsVo.setNewsChineseContent(translatedList.get(2*i+1));
        }
        getNewsVo.setNewsList(newsVoList);
        getNewsVo.setNewsSize(newsVoList.size());
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

    @Override
    public SearchNewsVo searchNews(SearchNewsRequest searchNewsRequest) {

        //存储查询好的新闻数据
        List<News> newsList = new ArrayList<>();
        List<News> cleanedNewsList = new ArrayList<>();
        SearchNewsVo searchNewsVo = new SearchNewsVo();
        Integer newsType = searchNewsRequest.getNewsType();
        String title = searchNewsRequest.getTitle();
        String content = searchNewsRequest.getContent();
        String startTime = searchNewsRequest.getStartTime();
        String endTime = searchNewsRequest.getEndTime();
        Integer isAll = searchNewsRequest.getIsAll();
        String Title = title;
        String Content = content;
        if(!content.equals("")){
            content = TranslateUtil.ChineseToJapanese(content);
        }
        if(!title.equals("")){
            title = TranslateUtil.ChineseToJapanese(title);
        }

        newsTypeEnum enumByValue = newsTypeEnum.getEnumByValue(newsType);
        if(enumByValue==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String type = enumByValue.getText();

        boolean isExplicitSearchTime = false;

        if(startTime!=null&&endTime!=null){
            if((!startTime.equals(""))&&(!endTime.equals(""))){
                if(!CommonUtils.isValidDateFormat(startTime)||!CommonUtils.isValidDateFormat(endTime)){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"搜索时间参数错误");
                }else{
                    isExplicitSearchTime = true;
                }
            }else if(startTime.equals("")&&endTime.equals("")){
                //此处不用做处理
            }else{
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"搜索时间参数错误");
            }
        }
        String path = System.getProperty("user.dir");
        String dirPath = path+File.separator+NewsFileConstant.TotalNewsFileDir+File.separator
                +NewsFileConstant.JapaneseNewsFileDir+File.separator
                +type+File.separator;
        //将所有需要的全量数据加载到内存中
        if(!isExplicitSearchTime){
            String systemTime = CommonUtils.getSystemTime();
            String preStrTime = systemTime.substring(0, 7);
            int day = Integer.parseInt(systemTime.substring(8));
            List<File> files = FileUtil.loopFiles(dirPath);
            if(files.size()<3){
                for(File file:files){
                    List<News> news = CommonUtils.readJsonFile(dirPath + file.getName());
                    newsList.addAll(news);
                }
            }else{
                //寻找三天的数据
                for(int i=0;i<3;++i){
                    String timePath = preStrTime+"-"+String.format("%02d", day - i)+".json";
                    for(File file:files){
                        String name = dirPath+file.getName();
                        if((name.equals(dirPath+timePath))){
                            //读取数据
                            List<News> news = CommonUtils.readJsonFile(dirPath + timePath);
                            newsList.addAll(news);
                        }
                    }
                }
            }
        }else{
            //根据给定的时间进行搜索
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date StartTime = dateFormat.parse(startTime);
                Date EndTime = dateFormat.parse(endTime);
                List<File> files = FileUtil.loopFiles(dirPath);
                for (File file : files) {
                    // 提取文件名中的日期部分
                    String fileName = file.getName();
                    String dateString = StrUtil.subBefore(fileName, ".", true);

                    // 解析日期
                    Date fileDate = dateFormat.parse(dateString);

                    // 检查文件日期是否在指定范围内
                    if ((fileDate.equals(StartTime) || fileDate.after(StartTime)) && (fileDate.equals(EndTime) || fileDate.before(EndTime))) {
                        List<News> news = CommonUtils.readJsonFile(dirPath+fileName);
                        newsList.addAll(news);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        boolean searchContent = (content!=null&&!content.equals(""));
        boolean searchTitle = (title!=null&&!title.equals(""));
        //将加载到内存中的全量数据进行数据筛选
        for (News news : newsList){
            String newsTitle = news.getNewsTitle();
            String newsContent = news.getNewsContent();
            if(searchContent&&!searchTitle){
                //只从新闻内容进行筛选
                if (newsContent.contains(content)||newsContent.contains(Content)){
                    cleanedNewsList.add(news);
                }
            }else if(searchTitle&&!searchContent){
                if(newsTitle.contains(title)||newsTitle.contains(Title)){
                    cleanedNewsList.add(news);
                }
            }else if(searchContent&&searchTitle){
                if((newsContent.contains(content)&&newsTitle.contains(title))||(newsContent.contains(Content)&&newsTitle.contains(Title))){
                    cleanedNewsList.add(news);
                }
            }
        }
        List<NewsVo> cleanedNewsVoList = new ArrayList<>();
        if(cleanedNewsList.size()==0){
            return searchNewsVo;
        }
        StringBuilder stringBuilder = new StringBuilder();
        //对返回的文本进行翻译
        for (int i=0;i<Math.min(cleanedNewsList.size(),50);++i){
            News news = cleanedNewsList.get(i);
            NewsVo newsVo = new NewsVo();
            newsVo.setNewsId(news.getNewsId());
            newsVo.setNewsTitle(news.getNewsTitle());
            newsVo.setNewsContent(news.getNewsContent());
            newsVo.setNewsTime(news.getNewsTime());
            newsVo.setNewsLink(news.getNewsLink());
            newsVo.setNewsImage(news.getNewsImage());
            cleanedNewsVoList.add(newsVo);
            if(i!=Math.min(cleanedNewsList.size(),50)-1){
                if(news.getNewsContent().length()>NewsConstant.NEWS_CONTENT_LENGTH) {
                    stringBuilder.append(news.getNewsTitle()).append("\n").append(news.getNewsContent(), 0, NewsConstant.NEWS_CONTENT_LENGTH).append("\n");
                }else{
                    stringBuilder.append(news.getNewsTitle()).append("\n").append(news.getNewsContent()).append("\n");
                }
            }
            else{
                if(news.getNewsContent().length()>NewsConstant.NEWS_CONTENT_LENGTH) {
                    stringBuilder.append(news.getNewsTitle()).append("\n").append(news.getNewsContent(), 0, NewsConstant.NEWS_CONTENT_LENGTH);
                }else{
                    stringBuilder.append(news.getNewsTitle()).append("\n").append(news.getNewsContent());
                }
            }
        }
        String str = stringBuilder.toString();
        List<String> translateList = TranslateUtil.batchTranslate(str);
        for(int i=0;i<cleanedNewsVoList.size();++i){
            cleanedNewsVoList.get(i).setNewsChineseTitle(translateList.get(2*i));
            cleanedNewsVoList.get(i).setNewsChineseContent(translateList.get(2*i+1));
        }
        newsList.clear();
        searchNewsVo.setNewsList(cleanedNewsVoList);
        return searchNewsVo;
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
            return new ArrayList<>();
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
