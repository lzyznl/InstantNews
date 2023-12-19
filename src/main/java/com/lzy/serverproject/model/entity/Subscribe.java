package com.lzy.serverproject.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName Subscribe
 */
@TableName(value ="Subscribe")
@Data
public class Subscribe implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户订阅手机号
     */
    private String phoneNumber;

    /**
     * 用户订阅天数
     */
    private Integer day;

    /**
     * 用户订阅新闻类型
     */
    private Integer newsType;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}