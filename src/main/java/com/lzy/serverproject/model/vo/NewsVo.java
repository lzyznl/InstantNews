package com.lzy.serverproject.model.vo;

import lombok.Data;

@Data
public class NewsVo {
    private Integer newsId;
    private String newsTitle;
    private String newsContent;
    private String newsChineseTitle;
    private String newsChineseContent;
    private String newsTime;
    private String newsLink;
    private String newsImage;
}
