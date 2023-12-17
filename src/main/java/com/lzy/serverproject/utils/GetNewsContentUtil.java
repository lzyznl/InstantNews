package com.lzy.serverproject.utils;

import com.google.gson.Gson;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.translate.translateResult;
import com.lzy.serverproject.utils.translate.TransApi;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Data
@Configuration
public class GetNewsContentUtil {


    /**
     * 获取新闻内容并翻译
     * @param newsList
     * @param isTranslate
     * @return
     */
    public static List<News> getNewsContent(List<News> newsList,Boolean isTranslate){
        if(newsList==null){
            return newsList;
        }
        TransApi transApi = new TransApi(NewsConstant.appId,NewsConstant.appKey);
        newsList.forEach(news->{
            Document document = null;
            try {
                document = Jsoup.connect(news.getNewsLink()).timeout(5000).get();
                String content = document.select(".sc-gLMgcV.EJLaQ.yjSlinkDirectlink.highLightSearchTarget").text();
                if(isTranslate){
                    //需要翻译
                    String transResult = transApi.getTransResult(content, NewsConstant.from_lg, NewsConstant.to_lg);
                    Gson gson = new Gson();
                    translateResult translateResult = gson.fromJson(transResult, translateResult.class);
                    news.setNewsContent(translateResult.getTrans_result()[0].getDst());
                }else{
                    //不需要翻译
                    news.setNewsContent(content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return newsList;
    }

}
