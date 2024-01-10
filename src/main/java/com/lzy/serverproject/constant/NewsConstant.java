package com.lzy.serverproject.constant;

import org.springframework.stereotype.Component;

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

    String NewsClassName = ".sc-cTsKDU.iKqGfS.yjSlinkDirectlink.highLightSearchTarget";

    Integer NEWS_CONTENT_LENGTH = 50;

    /**
     * 百度翻译相关参数设置
     */
    String appId = "20231009001841959";
    String appKey = "RRqR2QPLbfN33Em6rtCx";
    String from_lg = "jp";
    String to_lg = "zh";

}
