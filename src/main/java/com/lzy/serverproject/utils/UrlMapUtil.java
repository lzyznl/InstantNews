package com.lzy.serverproject.utils;

import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取新闻地址列表
 */
public class UrlMapUtil {

    public static Map<String, String> urlMap(){
        Map<String,String> urlMap = new HashMap<>();
        urlMap.put(NewsFileConstant.NewsType_ECONOMY,NewsConstant.ECONOMY);
        urlMap.put(NewsFileConstant.NewsType_LIVE,NewsConstant.LIVE);
        urlMap.put(NewsFileConstant.NewsType_IT,NewsConstant.IT);
        urlMap.put(NewsFileConstant.NewsType_INTERIOR,NewsConstant.INTERIOR);
        urlMap.put(NewsFileConstant.NewsType_INTERNATIONAL,NewsConstant.INTERNATIONAL);
        urlMap.put(NewsFileConstant.NewsType_RECREATION,NewsConstant.RECREATION);
        urlMap.put(NewsFileConstant.NewsType_PHYSICAL,NewsConstant.PHYSICAL);
        urlMap.put(NewsFileConstant.NewsType_GEO,NewsConstant.GEO);
        urlMap.put(NewsFileConstant.NewsType_SCIENCE,NewsConstant.SCIENCE);
        return urlMap;
    }
}
