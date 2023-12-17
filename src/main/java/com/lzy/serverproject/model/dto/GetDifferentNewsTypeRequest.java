package com.lzy.serverproject.model.dto;

import lombok.Data;

/**
 * 获取不同类型新闻的请求实体
 * @author lzy
 */
@Data
public class GetDifferentNewsTypeRequest {
    private int newsType;
    private int newsLange;
    private String newsTime;
    private int currentNewsNum;
    private int initSize;
    private int addSize;
}
