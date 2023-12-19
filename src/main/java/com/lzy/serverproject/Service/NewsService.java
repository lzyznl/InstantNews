package com.lzy.serverproject.Service;


import com.lzy.serverproject.model.dto.SearchNewsRequest;
import com.lzy.serverproject.model.vo.ExplicitNewsContentVo;
import com.lzy.serverproject.model.vo.NewsDataVo;
import com.lzy.serverproject.model.vo.SearchNewsVo;
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

    /**
     * 获取新闻发布数量数据
     * @return
     */
    NewsDataVo getNewsPubData();

    /**
     * 获取某个新闻的具体内容
     * @param newsType
     * @param newsTime
     * @param newsId
     * @return
     */
    ExplicitNewsContentVo getExplicitNewsContent(Integer newsType, String newsTime, Integer newsId);

    /**
     * 根据关键词查询新闻
     * @param searchNewsRequest
     * @return
     */
    SearchNewsVo searchNews(SearchNewsRequest searchNewsRequest);
}
