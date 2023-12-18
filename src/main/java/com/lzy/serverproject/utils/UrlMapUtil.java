package com.lzy.serverproject.utils;

import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.model.entity.News;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取新闻地址列表
 */
public class UrlMapUtil<T> {

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


    public  Map<String, List<T>> listMap(){
        Map<String,List<T>> listMap = new HashMap<>();
        listMap.put(NewsFileConstant.NewsType_ECONOMY,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_LIVE,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_IT,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_INTERIOR,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_INTERNATIONAL,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_RECREATION,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_PHYSICAL,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_GEO,new ArrayList<>());
        listMap.put(NewsFileConstant.NewsType_SCIENCE,new ArrayList<>());
        return listMap;
    }
}
