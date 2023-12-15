package com.lzy.serverproject.utils;

import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.model.News;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取新闻地址列表
 */
public class UrlListUtil {

    public static List<String> urlList(){
        List<String> urlList = new ArrayList<>();
        urlList.add(NewsConstant.LIVE);
        urlList.add(NewsConstant.IT);
        urlList.add(NewsConstant.SCIENCE);
        urlList.add(NewsConstant.ECONOMY);
        urlList.add(NewsConstant.INTERIOR);
        urlList.add(NewsConstant.INTERNATIONAL);
        urlList.add(NewsConstant.RECREATION);
        urlList.add(NewsConstant.PHYSICAL);
        urlList.add(NewsConstant.GEO);
        return urlList;
    }
}
