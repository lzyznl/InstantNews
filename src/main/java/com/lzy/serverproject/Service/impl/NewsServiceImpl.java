package com.lzy.serverproject.Service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.model.News;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.utils.SaveNewsListUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsServiceImpl implements NewsService {


    @Override
    public List<News> getNews(int newsType, int newsLange, String newsTime, int initSize, int addSize) {
        List<News> newsList = new ArrayList<>();
        newsTypeEnum newsEnum = newsTypeEnum.getEnumByValue(newsType);
        if(newsEnum==null){
            return newsList;
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
            return newsList;
        }
        //读取该文件中的所有内容
        List<News> newsLists = readJsonFile(path);
        //todo size这里还需要再确定一下
        return null;
    }

    public static List<News> getSubList(List<News> originalList, int sum) {
        if (originalList.size() <= sum) {
            // 如果原始列表大小小于或等于sum，返回整个原始列表
            return originalList;
        } else {
            // 使用subList截取前sum个元素
            return originalList.subList(0, sum);
        }
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
        if(newsTime!=null){
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
