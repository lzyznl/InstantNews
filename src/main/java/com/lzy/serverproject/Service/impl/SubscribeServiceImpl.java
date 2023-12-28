package com.lzy.serverproject.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzy.serverproject.SMS.SendSms;
import com.lzy.serverproject.model.entity.Subscribe;
import com.lzy.serverproject.Service.SubscribeService;
import com.lzy.serverproject.mapper.SubscribeMapper;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.model.vo.TypeNumVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        // 创建一个线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<String> list = new ArrayList<>();
        list.add(phoneNumber);
        // 异步执行sendMessage
        executor.submit(() -> {
            SendSms.sendMessage(newsTypeEnum.getEnumByValue(newsType).getText(),list);
        });
        // 关闭线程池（不立即关闭，但会在任务执行完后关闭）
        executor.shutdown();
        return insert>0;
    }

    @Override
    public TypeNumVo typeNum() {
        TypeNumVo typeNumVo = new TypeNumVo();
        Integer[] array = new Integer[9];
        for(int i=0;i<9;++i){
            array[i]=0;
        }
        List<Subscribe> subscribeList = subscribeMapper.selectList(null);
        for(int i=0;i<subscribeList.size();++i){
            Subscribe subscribe = subscribeList.get(i);
            Integer newsType = subscribe.getNewsType();
            array[newsType] = array[newsType]+1;
        }

        for(int i=0;i<9;++i){
            switch (i){
                case 0:
                    typeNumVo.setLiveNum(array[i]);
                    break;
                case 1:
                    typeNumVo.setInteriorNum(array[i]);
                    break;
                case 2:
                    typeNumVo.setInternationalNum(array[i]);
                    break;
                case 3:
                    typeNumVo.setEconomyNum(array[i]);
                    break;
                case 4:
                    typeNumVo.setRecreationNum(array[i]);
                    break;
                case 5:
                    typeNumVo.setPhysicalNum(array[i]);
                    break;
                case 6:
                    typeNumVo.setItNum(array[i]);
                    break;
                case 7:
                    typeNumVo.setScienceNum(array[i]);
                    break;
                case 8:
                    typeNumVo.setGeoNum(array[i]);
                    break;
                default:
                    break;
            }
        }
        return typeNumVo;
    }
}




