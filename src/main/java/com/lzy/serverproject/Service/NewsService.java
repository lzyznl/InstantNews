package com.lzy.serverproject.Service;


import com.lzy.serverproject.model.vo.getNewsVo;

public interface NewsService {

    /**
     * 获取新闻
     * @param newsType
     * @param newsLange
     * @param newsTime
     * @param initSize
     * @param addSize
     * @param currentNewsNum
     * @return
     */
    getNewsVo getNews(int newsType, int newsLange, String newsTime, int initSize, int addSize, int currentNewsNum);
}
