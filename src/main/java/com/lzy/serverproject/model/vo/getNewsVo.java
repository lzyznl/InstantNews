package com.lzy.serverproject.model.vo;

import com.lzy.serverproject.model.entity.News;
import lombok.Data;

import java.util.List;

@Data
public class getNewsVo {
    private List<NewsVo> newsList;
    private int newsSize;
}
