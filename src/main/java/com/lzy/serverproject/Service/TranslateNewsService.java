package com.lzy.serverproject.Service;

import com.lzy.serverproject.model.vo.TranslatedNewsVo;

/**
 * 翻译服务接口类
 */
public interface TranslateNewsService {
    /**
     * 翻译新闻
     * @param newsTime
     * @param newsType
     * @param newsId
     * @return
     */
    TranslatedNewsVo translate(String newsTime, Integer newsType, Integer newsId);

}
