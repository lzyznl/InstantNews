package com.lzy.serverproject.Controller;

import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.common.BaseResponse;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.common.ResultUtils;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.model.dto.GetDifferentNewsTypeRequest;
import com.lzy.serverproject.model.vo.getNewsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Resource
    private NewsService newsService;


    @PostMapping
    public BaseResponse<getNewsVo> getDifferentTypeNews(@RequestBody GetDifferentNewsTypeRequest getDifferentNewsTypeRequest){
        int newsType = getDifferentNewsTypeRequest.getNewsType();
        int newsLange = getDifferentNewsTypeRequest.getNewsLange();
        String newsTime = getDifferentNewsTypeRequest.getNewsTime();
        int initSize = getDifferentNewsTypeRequest.getInitSize();
        int addSize = getDifferentNewsTypeRequest.getAddSize();
        int currentNewsNum = getDifferentNewsTypeRequest.getCurrentNewsNum();
        if(newsLange<0||newsType>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(initSize==0&&addSize==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        getNewsVo returnNewsVo = newsService.getNews(newsType, newsLange, newsTime, initSize, addSize, currentNewsNum);
        return ResultUtils.success(returnNewsVo);
    }
}
