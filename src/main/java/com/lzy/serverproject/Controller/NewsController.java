package com.lzy.serverproject.Controller;

import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.common.BaseResponse;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.model.News;
import com.lzy.serverproject.model.dto.GetDifferentNewsTypeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Resource
    private NewsService newsService;


    @PostMapping
    public BaseResponse<List<News>> getDifferentTypeNews(@RequestBody GetDifferentNewsTypeRequest getDifferentNewsTypeRequest){
        int newsType = getDifferentNewsTypeRequest.getNewsType();
        int newsLange = getDifferentNewsTypeRequest.getNewsLange();
        String newsTime = getDifferentNewsTypeRequest.getNewsTime();
        int initSize = getDifferentNewsTypeRequest.getInitSize();
        int addSize = getDifferentNewsTypeRequest.getAddSize();
        if(newsLange<0||newsType>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(initSize==0&&addSize==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        newsService.getNews(newsType,newsLange,newsTime,initSize,addSize);
        return null;
    }
}
