package com.lzy.serverproject.model.dto;

import lombok.Data;

/**
 * @author lzy
 */
@Data
public class GetExplicitNewsContentRequest {
    private Integer newsType;
    private String newsTime;
    private Integer newsId;
}
