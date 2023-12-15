package com.lzy.serverproject.model;

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

    @Override
    public  int hashCode(){
        return newsTitle.hashCode();
    }
}
