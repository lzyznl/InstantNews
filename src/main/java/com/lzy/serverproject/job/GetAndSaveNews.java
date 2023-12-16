package com.lzy.serverproject.job;


import com.lzy.serverproject.model.News;
import com.lzy.serverproject.utils.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 获取新闻并且存储到文本当中
 * @author lzy
 */
@Component
public class GetAndSaveNews {

    List<News> preJapaneseNewsList = new ArrayList<>();
    List<News> preChineseNewsList = new ArrayList<>();



    @Scheduled(fixedRate = 7 * 60 * 1000)
    public void task(){
        System.out.println("开始执行定时任务");
        Map<String, String> urlMap = UrlMapUtil.urlMap();
        // 从线程池中获取一个线程，进行多线程工作
        ExecutorService executorService = NewsThreadProcessor.getExecutorService();
        for(Entry<String,String> entry:urlMap.entrySet()){
            String newsType = entry.getKey();
            String newsUrl = entry.getValue();
            System.out.println("开始爬取新闻"+newsType);
            System.out.println("爬取新闻地址"+newsUrl);
            // 提交任务
            Future<?> japaneseFuture = executorService.submit(() -> {
                System.out.println("开始爬取日文新闻");
                List<News> japaneseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getJapaneseNewsList(newsUrl), false);
                if (preJapaneseNewsList.size() == 0) {
                    //存储list中的内容
                    SaveNewsListUtil.save(japaneseNewsContentList, false, newsType);
                } else {
                    List<News> SaveJapaneseNewsList = new ArrayList<>();
                    //比较之后创建新list存储内容
                    for (News news : japaneseNewsContentList) {
                        for (News value : preJapaneseNewsList) {
                            if (!news.getNewsTitle().equals(value.getNewsTitle())) {
                                SaveJapaneseNewsList.add(news);
                            }
                        }
                    }
                    preJapaneseNewsList.clear();
                    preJapaneseNewsList.addAll(japaneseNewsContentList);
                    //存储list中的内容
                    SaveNewsListUtil.save(SaveJapaneseNewsList, false, newsType);
                }
                System.out.println("结束爬取日文新闻");
            });
            Future<?> chineseFuture = executorService.submit(() -> {
                System.out.println("开始爬取中文新闻");
                List<News> chineseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getChineseNewsList(newsUrl), true);
                if (preChineseNewsList.size() == 0) {
                    //直接存储内容
                    SaveNewsListUtil.save(chineseNewsContentList, true, newsType);
                } else {
                    List<News> SaveChineseNewsList = new ArrayList<>();
                    //比较之后存储内容
                    for (News news : chineseNewsContentList) {
                        for (News value : preChineseNewsList) {
                            if (!news.getNewsTitle().equals(value.getNewsTitle())) {
                                SaveChineseNewsList.add(news);
                            }
                        }
                    }
                    preChineseNewsList.clear();
                    preJapaneseNewsList.addAll(chineseNewsContentList);
                    //存储list中的内容
                    SaveNewsListUtil.save(SaveChineseNewsList, true, newsType);
                }
                System.out.println("爬取中文新闻结束");
            });
            try {
                japaneseFuture.get();
                chineseFuture.get();
            } catch (InterruptedException |ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("执行完成");
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
