package com.lzy.serverproject.utils.translate;

import com.google.gson.Gson;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.translate.trans_result;
import com.lzy.serverproject.model.translate.translateResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻译工具类
 */
public class TranslateUtil {

    static TransApi transApi = new TransApi(NewsConstant.appId,NewsConstant.appKey);

    public static String getTranslateResult(String query){
        String transResult = transApi.getTransResult(query, NewsConstant.from_lg, NewsConstant.to_lg);
        Gson gson = new Gson();
        translateResult translateResult = gson.fromJson(transResult, translateResult.class);
        return translateResult.getTrans_result()[0].getDst();
    }

    public static String ChineseToJapanese(String query){
        String transResult = transApi.getTransResult(query, NewsConstant.to_lg, NewsConstant.from_lg);
        Gson gson = new Gson();
        translateResult translateResult = gson.fromJson(transResult, translateResult.class);
        return translateResult.getTrans_result()[0].getDst();
    }

    public static List<String> batchTranslate(String query){
        List<String> list = new ArrayList<>();
        String transResult = transApi.getTransResult(query, NewsConstant.from_lg, NewsConstant.to_lg);
        Gson gson = new Gson();
        translateResult translateResult = gson.fromJson(transResult, translateResult.class);
        trans_result[] trans_result = translateResult.getTrans_result();
        for(int i=0;i<trans_result.length;++i){
            list.add(trans_result[i].getDst());
        }
        return list;
    }

    /**
     * 翻译日文新闻list
     * @param translateNewsList
     * @return
     */
    public static List<News> translate(List<News> translateNewsList){
        if(translateNewsList==null){
            return null;
        }
        List<News> translatedNewsList = new ArrayList<>();
        //先进行简单的线性翻译
        for(int i=0;i<translateNewsList.size();++i){
            News translatedNews = new News();
            News news = translateNewsList.get(i);
            translatedNews.setNewsImage(news.getNewsImage());
            translatedNews.setNewsTime(news.getNewsTime());
            translatedNews.setNewsLink(news.getNewsLink());
            String translatedNewsTitle = transApi.getTransResult(news.getNewsTitle(), NewsConstant.from_lg, NewsConstant.to_lg);
            String translatedNewsContent = transApi.getTransResult(news.getNewsContent(), NewsConstant.from_lg, NewsConstant.to_lg);
            translatedNews.setNewsContent(translatedNewsContent);
            translatedNews.setNewsTitle(translatedNewsTitle);

            translatedNewsList.add(translatedNews);
        }
        return translatedNewsList;
    }
}
