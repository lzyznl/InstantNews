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
        urlMap.put(NewsFileConstant.ChineseNewsType_ECONOMY,NewsConstant.ECONOMY);
        urlMap.put(NewsFileConstant.ChineseNewsType_LIVE,NewsConstant.LIVE);
        urlMap.put(NewsFileConstant.ChineseNewsType_IT,NewsConstant.IT);
        urlMap.put(NewsFileConstant.ChineseNewsType_INTERIOR,NewsConstant.INTERIOR);
        urlMap.put(NewsFileConstant.ChineseNewsType_INTERNATIONAL,NewsConstant.INTERNATIONAL);
        urlMap.put(NewsFileConstant.ChineseNewsType_RECREATION,NewsConstant.RECREATION);
        urlMap.put(NewsFileConstant.ChineseNewsType_PHYSICAL,NewsConstant.PHYSICAL);
        urlMap.put(NewsFileConstant.ChineseNewsType_GEO,NewsConstant.GEO);
        urlMap.put(NewsFileConstant.ChineseNewsType_SCIENCE,NewsConstant.SCIENCE);
        return urlMap;
    }
}
