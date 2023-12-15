package com.lzy.serverproject.constant;

import com.lzy.serverproject.model.News;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public interface NewsConstant {
    /**
     * 各种新闻类型
     */
    String LIVE = "https://news.yahoo.co.jp/rss/categories/life.xml";
    String INTERIOR = "https://news.yahoo.co.jp/rss/categories/domestic.xml";
    String INTERNATIONAL = "https://news.yahoo.co.jp/rss/categories/world.xml";
    String ECONOMY = "https://news.yahoo.co.jp/rss/categories/business.xml";
    String RECREATION = "https://news.yahoo.co.jp/rss/categories/entertainment.xml";
    String PHYSICAL = "https://news.yahoo.co.jp/rss/categories/sports.xml";
    String IT = "https://news.yahoo.co.jp/rss/categories/it.xml";
    String SCIENCE = "https://news.yahoo.co.jp/rss/categories/science.xml";
    String GEO = "https://news.yahoo.co.jp/rss/categories/local.xml";


    /**
     * 百度翻译相关参数设置
     */
    String appId = "20231007001839386";
    String appKey = "LIKxDadYENAgI5AoGUaM";
    String from_lg = "jp";
    String to_lg = "zh";
}
