package com.lzy.serverproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzy.serverproject.model.entity.Subscribe;
import com.lzy.serverproject.Service.SubscribeService;
import com.lzy.serverproject.mapper.SubscribeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 86185
* @description 针对表【Subscribe】的数据库操作Service实现
* @createDate 2023-12-19 21:02:46
*/
@Service
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe>
    implements SubscribeService{


    @Resource
    private SubscribeMapper subscribeMapper;
    /**
     * 订阅某条具体新闻
     * @param phoneNumber
     * @param newsType
     * @param subscribe
     * @return
     */
    @Override
    public Boolean subscribe(String phoneNumber, Integer newsType, Integer subscribe) {
        Subscribe subscribeModel = new Subscribe();
        subscribeModel.setNewsType(newsType);
        subscribeModel.setPhoneNumber(phoneNumber);
        subscribeModel.setDay(subscribe);
        int insert = subscribeMapper.insert(subscribeModel);
        return insert>0;
    }
}




