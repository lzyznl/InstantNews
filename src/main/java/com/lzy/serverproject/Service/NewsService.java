package com.lzy.serverproject.Service;


import com.lzy.serverproject.model.News;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NewsService {

    /**
     * 获取新闻
     * @param newsType
     * @param newsLange
     * @param newsTime
     * @param initSize
     * @param addSize
     * @return
     */
    List<News> getNews(int newsType, int newsLange, String newsTime, int initSize, int addSize);
}
