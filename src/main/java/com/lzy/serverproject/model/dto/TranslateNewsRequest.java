package com.lzy.serverproject.model.dto;

import lombok.Data;

/**
 * 翻译新闻请求类
 */
@Data
public class TranslateNewsRequest {
    private String newsTime;
    private Integer newsType;
    private Integer newsId;
}
