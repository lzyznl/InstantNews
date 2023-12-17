package com.lzy.serverproject.Service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.model.vo.getNewsVo;
import com.lzy.serverproject.utils.SaveNewsListUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NewsServiceImpl implements NewsService {


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
        List<News> newsLists = readJsonFile(path);
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

    /**
     * 读取json文件中的内容并且转化为list
     * @param filePath
     * @return
     */
    public static List<News> readJsonFile(String filePath) {
        List<News> newsList = new ArrayList<>();

        // 读取文件内容
        String jsonContent = FileUtil.readUtf8String(filePath);

        // 使用Hutool解析JSON
        JSONArray jsonArray = JSONUtil.parseArray(jsonContent);

        // 遍历JSON数组，将每个JSON对象转换为News对象
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            News news = new News();
            news.setNewsTitle(jsonObj.getStr("newsTitle"));
            news.setNewsContent(jsonObj.getStr("newsContent"));
            news.setNewsTime(jsonObj.getStr("newsTime"));
            news.setNewsLink(jsonObj.getStr("newsLink"));
            news.setNewsImage(jsonObj.getStr("newsImage"));
            newsList.add(news);
        }

        return newsList;
    }

    public String getPath(String type,String newsTime,boolean isChinese){
        String projectFilePath = System.getProperty("user.dir");
        String newsFilePath = "";
        String finalPath = "";
        String systemTime = SaveNewsListUtil.getSystemTime();
        if(isChinese){
            newsFilePath=projectFilePath+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator+NewsFileConstant.ChineseNewsFileDir
                    +File.separator+type;
        }else{
            newsFilePath=projectFilePath+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator+NewsFileConstant.JapaneseNewsFileDir
                    +File.separator+type;
        }
        if(!newsTime.equals("")){
            if(!isValidDateFormat(newsTime)){
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

    public static boolean isValidDateFormat(String date) {
        // 使用正则表达式匹配日期格式
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(date);

        return matcher.matches();
    }
}
