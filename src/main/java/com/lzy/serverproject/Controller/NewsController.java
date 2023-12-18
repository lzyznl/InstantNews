package com.lzy.serverproject.Controller;

import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.Service.TranslateNewsService;
import com.lzy.serverproject.common.BaseResponse;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.common.ResultUtils;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.model.dto.GetDifferentNewsTypeRequest;
import com.lzy.serverproject.model.dto.GetExplicitNewsContentRequest;
import com.lzy.serverproject.model.dto.TranslateNewsRequest;
import com.lzy.serverproject.model.vo.ExplicitNewsContentVo;
import com.lzy.serverproject.model.vo.NewsDataVo;
import com.lzy.serverproject.model.vo.TranslatedNewsVo;
import com.lzy.serverproject.model.vo.getNewsVo;
import com.lzy.serverproject.utils.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 新闻接口
 * @author lzy
 */
@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Resource
    private NewsService newsService;

    @Resource
    private TranslateNewsService translateNewsService;


    /**
     * 获取新闻接口
     * @param getDifferentNewsTypeRequest
     * @return
     */
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


    /**
     * 翻译某条新闻接口
     */
    @PostMapping("/translate")
    public BaseResponse<TranslatedNewsVo> translateNews(@RequestBody TranslateNewsRequest translateNewsRequest){
        String newsTime = translateNewsRequest.getNewsTime();
        Integer newsType = translateNewsRequest.getNewsType();
        Integer newsId = translateNewsRequest.getNewsId();
        if(newsId<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(newsType<0||newsType>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(newsTime!=null&&!newsTime.equals("")&&!CommonUtils.isValidDateFormat(newsTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TranslatedNewsVo translate = translateNewsService.translate(newsTime, newsType, newsId);
        return ResultUtils.success(translate);
    }


    /**
     * 获取新闻发布数量相关信息接口
     * @return
     */
    @GetMapping("/getNewsPubData")
    public BaseResponse<NewsDataVo> getNewsData(){
        NewsDataVo newsPubData = newsService.getNewsPubData();
        return ResultUtils.success(newsPubData);
    }


    @PostMapping("/get")
    public BaseResponse<ExplicitNewsContentVo> getExplicitNewsContent(@RequestBody GetExplicitNewsContentRequest getExplicitNewsContentRequest){
        String newsTime = getExplicitNewsContentRequest.getNewsTime();
        Integer newsType = getExplicitNewsContentRequest.getNewsType();
        Integer newsId = getExplicitNewsContentRequest.getNewsId();
        if(newsId<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(newsType<0||newsType>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(newsTime!=null&&!newsTime.equals("")&&!CommonUtils.isValidDateFormat(newsTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ExplicitNewsContentVo explicitNewsContent = newsService.getExplicitNewsContent(newsType, newsTime, newsId);
        return ResultUtils.success(explicitNewsContent);
    }
}
