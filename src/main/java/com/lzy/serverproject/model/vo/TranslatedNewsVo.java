package com.lzy.serverproject.model.vo;

import lombok.Data;

/**
 * 翻译新闻返回类
 * @author lzy
 */
@Data
public class TranslatedNewsVo {
    private String newsTitle;
    private String newsContent;
    private String newsTime;
}
