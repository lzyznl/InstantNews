package com.lzy.serverproject.utils.news;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.utils.common.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * 存储新闻列表工具类
 * @author lzy
 */
public class SaveNewsListUtil {


    /**
     * 存储新闻列表中的新闻到文件
     * @param newsList
     * @param isChinese 表明存储内容是否是新闻
     * @return
     */
    public static Boolean save(List<News> newsList,Boolean isChinese,String NewsType){
        String FileDir = System.getProperty("user.dir");
        String TotalNewsFileDir = FileDir+File.separator+NewsFileConstant.TotalNewsFileDir;
        checkFileExistsOrMake(TotalNewsFileDir,true);
        if(isChinese){
            saveToAppointedFile(TotalNewsFileDir,true,NewsType,newsList);
        }else{
            saveToAppointedFile(TotalNewsFileDir,false,NewsType,newsList);
        }
        return true;
    }

    public static void saveToAppointedFile(String TotalNewsFileDir,Boolean isChinese,String NewsType,List<News> newsList){
        //向日文文件夹下存储
        String NewsFileDir = "";
        if(isChinese){
            NewsFileDir = TotalNewsFileDir+ File.separator+ NewsFileConstant.ChineseNewsFileDir;
        }else{
            NewsFileDir = TotalNewsFileDir+ File.separator+ NewsFileConstant.JapaneseNewsFileDir;
        }
        checkFileExistsOrMake(NewsFileDir,true);
        String TypeNewsFileDir = NewsFileDir+File.separator+NewsType;
        checkFileExistsOrMake(TypeNewsFileDir,true);
        String systemTime = CommonUtils.getSystemTime();
        //创建具体的新闻文件
        String explicitNewsFile = TypeNewsFileDir+File.separator+(systemTime+".json");
        checkFileExistsOrMake(explicitNewsFile,false);
        //向该文件中写入内容
        // 读取已有的JSON文件内容
        JSONArray existingNewsArray = readExistingNews(explicitNewsFile);
        // 将新的News对象合并到已有的JSON数组中
        JSONArray combinedNewsArray = mergeNews(existingNewsArray, newsList);
        // 将合并后的JSON数组写入文件
        writeNewsToFile(combinedNewsArray,explicitNewsFile);
    }


    /**
     * 检查某个文件是否存在，如果存在则创建
     * @param filePath
     */
    public static void checkFileExistsOrMake(String filePath,Boolean isDir){
        if(!FileUtil.exist(filePath)){
            if(isDir){
                FileUtil.mkdir(filePath);
            }else{
                FileUtil.touch(filePath);
            }
        }
    }

    private static JSONArray readExistingNews(String JSON_FILE_PATH) {
        File jsonFile = FileUtil.file(JSON_FILE_PATH);
        // 如果文件不存在，返回一个空的JSONArray
        if (!jsonFile.exists()) {
            return new JSONArray();
        }
        // 读取文件内容并解析为JSONArray
        String jsonStr = FileUtil.readUtf8String(jsonFile);
        // 判断字符串是否为空，如果为空，返回一个空的JSONArray
        if (StringUtils.isBlank(jsonStr)) {
            return new JSONArray();
        }
        return JSONUtil.parseArray(jsonStr);
    }

    private static JSONArray mergeNews(JSONArray existingNewsArray, List<News> newNewsList) {
        // 将新的News对象转换为JSONArray
        JSONArray newNewsArray = JSONUtil.parseArray(newNewsList);
        // 合并已有和新的News数组
        existingNewsArray.addAll(newNewsArray);
        return existingNewsArray;
    }

    private static void writeNewsToFile(JSONArray newsArray,String JSON_FILE_PATH) {
        // 将合并后的JSONArray转为JSON字符串
        String jsonStr = newsArray.toStringPretty();
        // 将JSON字符串写入文件
        FileUtil.writeUtf8String(jsonStr, JSON_FILE_PATH);
    }
}
