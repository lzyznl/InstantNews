package com.lzy.serverproject.utils.translate;

import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.model.entity.News;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻译工具类
 */
public class translateUtil {

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
            TransApi transApi = new TransApi(NewsConstant.appId,NewsConstant.appKey);
            String translatedNewsTitle = transApi.getTransResult(news.getNewsTitle(), NewsConstant.from_lg, NewsConstant.to_lg);
            String translatedNewsContent = transApi.getTransResult(news.getNewsContent(), NewsConstant.from_lg, NewsConstant.to_lg);
            translatedNews.setNewsContent(translatedNewsContent);
            translatedNews.setNewsTitle(translatedNewsTitle);

            translatedNewsList.add(translatedNews);
        }
        return translatedNewsList;
    }
}
