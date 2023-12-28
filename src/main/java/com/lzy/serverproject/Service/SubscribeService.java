package com.lzy.serverproject.Service;

import com.lzy.serverproject.model.entity.Subscribe;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzy.serverproject.model.vo.TypeNumVo;

/**
* @author 86185
* @description 针对表【Subscribe】的数据库操作Service
* @createDate 2023-12-19 21:02:46
*/
public interface SubscribeService extends IService<Subscribe> {

    /**
     * 订阅某条具体新闻
     * @param phoneNumber
     * @param newsType
     * @param subscribe
     * @return
     */
    Boolean subscribe(String phoneNumber, Integer newsType, Integer subscribe);

    /**
     * 获取每个栏目的订阅人数
     * @return
     */
    TypeNumVo typeNum();
}
