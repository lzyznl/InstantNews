package com.lzy.serverproject.utils;

import com.lzy.serverproject.job.AIJob;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AITest {
    
    @Test
    public void test(){
        String newsType = "science";
        AIJob aiJob = new AIJob();
        List<String> list = aiJob.getAISummarizedNewsContent(newsType);
        for (String str:list){
            System.out.println(str);
        }
    }
}
