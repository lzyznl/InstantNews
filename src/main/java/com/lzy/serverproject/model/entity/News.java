package com.lzy.serverproject.model.entity;

import lombok.Data;

/**
 * 新闻类
 */
@Data
public class News {
    private String newsTitle;
    private String newsContent;
    private String newsTime;
    private String newsLink;
    private String newsImage;

    @Override
    public  int hashCode(){
        return newsTitle.hashCode();
    }
}
