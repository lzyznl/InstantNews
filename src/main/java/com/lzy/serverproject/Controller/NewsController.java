package com.lzy.serverproject.Controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lzy.serverproject.Service.NewsService;
import com.lzy.serverproject.Service.TranslateNewsService;
import com.lzy.serverproject.common.BaseResponse;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.common.ResultUtils;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.model.dto.GetDifferentNewsTypeRequest;
import com.lzy.serverproject.model.dto.GetExplicitNewsContentRequest;
import com.lzy.serverproject.model.dto.SearchNewsRequest;
import com.lzy.serverproject.model.dto.TranslateNewsRequest;
import com.lzy.serverproject.model.vo.*;
import com.lzy.serverproject.utils.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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


    /**
     * 查看某个新闻的具体内容
     * @param getExplicitNewsContentRequest
     * @return
     */
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

    /**
     * 根据关键词查询新闻
     * @param searchNewsRequest
     * @return
     */
    @PostMapping("/searchNews")
    public  BaseResponse<SearchNewsVo> searchNews(@RequestBody SearchNewsRequest searchNewsRequest){
        SearchNewsVo searchNewsVo = newsService.searchNews(searchNewsRequest);
        return ResultUtils.success(searchNewsVo);
    }

    /**
     * 获取新闻数据开始时间和结束时间
     * @return
     */
    @GetMapping("/date")
    public BaseResponse<DateTimeVo> getDate(){
        DateTimeVo dateTimeVo = new DateTimeVo();
        String path = System.getProperty("user.dir");
        String filePath = path+ File.separator+ NewsFileConstant.TotalNewsFileDir+File.separator
                +NewsFileConstant.JapaneseNewsFileDir+File.separator
                +NewsFileConstant.NewsType_LIVE;
        List<File> fileList = FileUtil.loopFiles(filePath);
        if (!fileList.isEmpty()) {
            Date earliestDate = null;
            Date latestDate = null;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (File file : fileList) {
                String fileName = file.getName();
                String dateString = StrUtil.subBefore(fileName, ".", true);

                try {
                    Date fileDate = dateFormat.parse(dateString);

                    if (earliestDate == null || fileDate.before(earliestDate)) {
                        earliestDate = fileDate;
                    }

                    if (latestDate == null || fileDate.after(latestDate)) {
                        latestDate = fileDate;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (earliestDate != null && latestDate != null) {
                dateTimeVo.setStartTime(dateFormat.format(earliestDate));
                dateTimeVo.setEndTime(dateFormat.format(latestDate));
            }
        }
        return ResultUtils.success(dateTimeVo);
    }
}
