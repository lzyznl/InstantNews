package com.lzy.serverproject.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName DayNewsNum
 */
@TableName(value ="DayNewsNum")
@Data
public class DayNewsNum implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 时间
     */
    private String dayTime;

    /**
     * 生活模块新闻数量
     */
    private Integer liveNum;

    /**
     * 经济新闻模块数量
     */
    private Integer economyNum;

    /**
     * 地理模块新闻数量
     */
    private Integer geoNum;

    /**
     * 国内模块新闻数量
     */
    private Integer interiorNum;

    /**
     * 国际模块新闻数量
     */
    private Integer interNationalNum;

    /**
     * IT模块新闻数量
     */
    private Integer ITNum;

    /**
     * 健康模块新闻数量
     */
    private Integer physicalNum;

    /**
     * 娱乐模块新闻数量
     */
    private Integer recreationNum;

    /**
     * 科学模块新闻数量
     */
    private Integer scienceNum;

    /**
     * 全天新闻总数量
     */
    private Long allNum;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}