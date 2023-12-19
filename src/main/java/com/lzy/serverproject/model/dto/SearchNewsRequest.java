package com.lzy.serverproject.model.dto;

import lombok.Data;

@Data
public class SearchNewsRequest {
    private Integer newsType;
    private String title;
    private String content;
    private String startTime;
    private String endTime;
    private Integer isAll;
    private Integer initSize;
    private Integer currentNewsNum;
    private Integer addSize;
}
