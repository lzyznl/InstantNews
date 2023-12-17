package com.lzy.serverproject.model.enums;

import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum newsTypeEnum {
    LIVE(NewsFileConstant.NewsType_LIVE,0),
    INTERIOR(NewsFileConstant.NewsType_INTERIOR,1),
    INTERNATIONAL(NewsFileConstant.NewsType_INTERNATIONAL,2),
    ECONOMY(NewsFileConstant.NewsType_ECONOMY,3),
    RECREATION(NewsFileConstant.NewsType_RECREATION,4),
    PHYSICAL(NewsFileConstant.NewsType_PHYSICAL,5),
    IT(NewsFileConstant.NewsType_IT,6),
    SCIENCE(NewsFileConstant.NewsType_SCIENCE,7),
    GEO(NewsFileConstant.NewsType_GEO,8);

    private String text;
    private Integer value;

    newsTypeEnum(String text, Integer value){
        this.text = text;
        this.value = value;
    }

    public static List<Integer> getValues(){
        List<Integer> ValuesList = Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
        return ValuesList;
    }

    public static newsTypeEnum getEnumByValue(Integer value){
        if(ObjectUtils.isEmpty(value)){
            return null;
        }
        for(newsTypeEnum newsTypeEnum:newsTypeEnum.values()){
            if(newsTypeEnum.value.equals(value)){
                return newsTypeEnum;
            }
        }
        return null;
    }

    public String getText(){
        return this.text;
    }

    public int getValue(){
        return this.value;
    }
}
