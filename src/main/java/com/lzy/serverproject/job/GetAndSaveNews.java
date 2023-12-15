package com.lzy.serverproject.job;


import com.lzy.serverproject.model.News;
import com.lzy.serverproject.utils.GetNewsContentUtil;
import com.lzy.serverproject.utils.GetNewsListUtil;
import com.lzy.serverproject.utils.UrlListUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取新闻并且存储到文本当中
 * @author lzy
 */
@Component
public class GetAndSaveNews {

    List<News> preJapaneseNewsList = new ArrayList<>();
    List<News> preChineseNewsList = new ArrayList<>();


    @Scheduled(cron = "0 */5 * * * ?")
    public void task(){
        List<String> urlList = UrlListUtil.urlList();
        urlList.forEach(url->{
            //获取日文标题以及日文正文内容
            List<News> japaneseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getJapaneseNewsList(url), false);
            //获取中文标题
            List<News> chineseNewsContentList = GetNewsContentUtil.getNewsContent(GetNewsListUtil.getChineseNewsList(url), true);
            if(preJapaneseNewsList==null){
                //存储list中的内容
            }else{
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

            }

            if(preChineseNewsList==null){
                //直接存储内容
            }else{
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

            }
        });
    }
}
