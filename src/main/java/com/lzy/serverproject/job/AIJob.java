package com.lzy.serverproject.job;


import com.lzy.serverproject.AI.BigModelNew;
import com.lzy.serverproject.SMS.SendSms;
import com.lzy.serverproject.constant.AIConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.mapper.DayNewsNumMapper;
import com.lzy.serverproject.mapper.SubscribeMapper;
import com.lzy.serverproject.model.entity.DayNewsNum;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.entity.Subscribe;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.utils.UrlMapUtil;
import com.lzy.serverproject.utils.common.CommonUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时总结最新新闻内容发送给用户
 * @author lzy
 */
public class AIJob {

    @Resource
    private DayNewsNumMapper dayNewsNumMapper;

    @Resource
    private SubscribeMapper subscribeMapper;

    Map<String, List<String>> AIContentMap = UrlMapUtil.AISummarizedNewsListMap();

    /**
     * 总结新闻定时任务
     */
    public void task(){
        //先从数据库中查询所有的订阅信息
        List<Subscribe> subscribeList = subscribeMapper.selectList(null);
        for(int i=0;i<subscribeList.size();++i){
            Subscribe subscribe = subscribeList.get(i);
            Integer newsType = subscribe.getNewsType();
            String phoneNumber = subscribe.getPhoneNumber();
            newsTypeEnum enumByValue = newsTypeEnum.getEnumByValue(newsType);
            String type = "";
            if(enumByValue!=null){
                type=enumByValue.getText();
            }
            List<String> list = new ArrayList<>();
            list.add(phoneNumber);
            SendSms.sendMessage(type,list);
        }
    }


    public List<String> getAISummarizedNewsContent(String newsTpe){
        String systemTime = CommonUtils.getSystemTime();
        //构造文件路径
        String path = System.getProperty("user.dir");
        String finalPath = path+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator
                +NewsFileConstant.JapaneseNewsFileDir+File.separator+newsTpe+File.separator+(systemTime+".json");
        //判断全局变量中是否存在
        List<String> contentList = AIContentMap.get(newsTpe);
        if(contentList.size()!=0){
            return contentList;
        }
        //从文件中进行读取
        List<News> newsList = CommonUtils.readJsonFile(finalPath);
        List<String> AIContentList = new ArrayList<>();
        for(int i=0;i<10;++i){
            //对每一条新闻进行总结
            String content = newsList.get(i).getNewsContent();
            String aiContent = BigModelNew.getAIContent(AIConstant.preStr+content);
            if(aiContent.length()>AIConstant.AIContentNewsLength){
                aiContent = aiContent.substring(0,AIConstant.AIContentNewsLength);
            }
            System.out.println(i+":"+aiContent);
            AIContentList.add(aiContent);
        }
        AIContentMap.replace(newsTpe,AIContentList);
        return AIContentList;
    }
}
