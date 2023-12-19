package com.lzy.serverproject.model.vo;

import com.lzy.serverproject.model.entity.News;
import lombok.Data;

import java.util.List;

@Data
public class SearchNewsVo {
    private List<News> newsList;
    private String newsTime;
}
