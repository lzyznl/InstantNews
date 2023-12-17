package com.lzy.serverproject.utils;

import com.google.gson.Gson;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.translate.translateResult;
import com.lzy.serverproject.utils.translate.TransApi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取新闻列表类
 */
public class GetNewsListUtil {

    public static List<News> fetchUrlContent(String url, List<News> newsList){
        try {
            Document document = Jsoup.connect(url).get();
            Elements itemList = document.select("item");
            for(int i=0;i<20;++i){
                Element newsItem = itemList.get(i);
                String title = newsItem.select("title").text();
                String link = newsItem.select("link").text();
                String pubDate = newsItem.select("pubDate").text();
                String image = newsItem.select("image").text();
                News news = new News();
                news.setNewsTitle(title);
                news.setNewsTime(pubDate);
                news.setNewsLink(link);
                news.setNewsImage(image);
                newsList.add(news);
            }
            return newsList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    public static List<News> getJapaneseNewsList(String newsUrl){
        List<News> newsList = new ArrayList<>();
        List<News> japaneseNewsList = fetchUrlContent(newsUrl, newsList);
        return japaneseNewsList;
    }

    public static List<News> getChineseNewsList(String newsUrl){
        List<News> newsList = new ArrayList<>();
        List<News> chineseNewsList = fetchUrlContent(newsUrl, newsList);
        TransApi transApi = new TransApi(NewsConstant.appId,NewsConstant.appKey);
        chineseNewsList.forEach(newsItem->{
            String transResult = transApi.getTransResult(newsItem.getNewsTitle(), NewsConstant.from_lg, NewsConstant.to_lg);
            Gson gson = new Gson();
            translateResult translateResult = gson.fromJson(transResult, translateResult.class);
            newsItem.setNewsTitle(translateResult.getTrans_result()[0].getDst());
        });
        return chineseNewsList;
    }
}
