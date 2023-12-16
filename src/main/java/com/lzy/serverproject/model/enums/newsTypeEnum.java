package com.lzy.serverproject.model.enums;

import com.lzy.serverproject.constant.NewsConstant;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum newsTypeEnum {
    LIVE(NewsConstant.LIVE,0),
    INTERIOR(NewsConstant.INTERIOR,1),
    INTERNATIONAL(NewsConstant.INTERNATIONAL,2),
    ECONOMY(NewsConstant.ECONOMY,3),
    RECREATION(NewsConstant.RECREATION,4),
    PHYSICAL(NewsConstant.PHYSICAL,5),
    IT(NewsConstant.IT,6),
    SCIENCE(NewsConstant.SCIENCE,7),
    GEO(NewsConstant.GEO,8);

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
