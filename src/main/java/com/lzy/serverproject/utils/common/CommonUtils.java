package com.lzy.serverproject.utils.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.serverproject.model.entity.News;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自己创建的通用开发工具类
 */
public class CommonUtils {
    /**
     * 获取系统当前时间
     * @return
     */
    public static String getSystemTime(){
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化当前时间为指定格式的字符串
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
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
            news.setNewsId(Integer.valueOf(jsonObj.getStr("newsId")));
            news.setNewsTitle(jsonObj.getStr("newsTitle"));
            news.setNewsContent(jsonObj.getStr("newsContent"));
            news.setNewsTime(jsonObj.getStr("newsTime"));
            news.setNewsLink(jsonObj.getStr("newsLink"));
            news.setNewsImage(jsonObj.getStr("newsImage"));
            newsList.add(news);
        }

        return newsList;
    }

    /**
     * 查看时间是否匹配固定格式
     * @param date
     * @return
     */
    public static boolean isValidDateFormat(String date) {
        // 使用正则表达式匹配日期格式
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(date);

        return matcher.matches();
    }
}
