package com.lzy.serverproject.utils;


import com.lzy.serverproject.SMS.SendSms;
import com.lzy.serverproject.job.AIJob;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SMSTest {


    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("18535475075");
        SendSms.sendMessage("live",list);
        System.out.println("短信发送成功");


//        //String[] templateParamSet,String[] phoneNumberSet
//        AIJob aiJob = new AIJob();
//        List<String> live = aiJob.getAISummarizedNewsContent("economy");
//        String[] templateParamSet = new String[11];
//        templateParamSet[0]="live";
//        for(int i=0;i<live.size();++i){
//            templateParamSet[i+1]=live.get(i);
//        }
//        String[] phoneNumberSet = new String[1];
//        phoneNumberSet[0]="+8618535475075";
//        SendSms.send(templateParamSet,phoneNumberSet);
//        System.out.println("短信发送成功");
    }
}
