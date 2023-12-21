package com.lzy.serverproject.job;

import com.lzy.serverproject.mapper.DayNewsNumMapper;
import com.lzy.serverproject.model.entity.DayNewsNum;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时总结最新新闻内容发送给用户
 * @author lzy
 */
public class AIJob {

    @Resource
    private DayNewsNumMapper dayNewsNumMapper;

    /**
     * 总结新闻定时任务
     */
    public void task(){
        //先从数据库中查询所有的订阅信息
        List<DayNewsNum> dayNewsNumList = dayNewsNumMapper.selectList(null);
        if(dayNewsNumList.size()==0) {
            return ;
        }
    }
}
