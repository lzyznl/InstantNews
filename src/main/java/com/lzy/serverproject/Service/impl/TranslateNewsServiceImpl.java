package com.lzy.serverproject.Service.impl;

import cn.hutool.core.io.FileUtil;
import com.lzy.serverproject.Service.TranslateNewsService;
import com.lzy.serverproject.common.ErrorCode;
import com.lzy.serverproject.constant.NewsConstant;
import com.lzy.serverproject.constant.NewsFileConstant;
import com.lzy.serverproject.exception.BusinessException;
import com.lzy.serverproject.model.entity.News;
import com.lzy.serverproject.model.enums.newsTypeEnum;
import com.lzy.serverproject.model.vo.TranslatedNewsVo;
import com.lzy.serverproject.utils.UrlMapUtil;
import com.lzy.serverproject.utils.common.CommonUtils;
import com.lzy.serverproject.utils.translate.TransApi;
import com.lzy.serverproject.utils.translate.TranslateUtil;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class TranslateNewsServiceImpl implements TranslateNewsService {

    private static Map<String,List<News>> map = UrlMapUtil.listMap();

    /**
     * 翻译新闻
     * @param newsTime
     * @param newsType
     * @param newsId
     * @return
     */
    @Override
    public TranslatedNewsVo translate(String newsTime, Integer newsType, Integer newsId) {
        TranslatedNewsVo translatedNewsVo = new TranslatedNewsVo();
        newsTypeEnum enumByValue = newsTypeEnum.getEnumByValue(newsType);
        TransApi transApi = new TransApi(NewsConstant.appId,NewsConstant.appKey);
        if(enumByValue==null){
            return translatedNewsVo;
        }
        //todo 这里可以先存入map当中
        String type = enumByValue.getText();
        String path = System.getProperty("user.dir");
        String dirPath = path+ File.separator + NewsFileConstant.TotalNewsFileDir+File.separator+
                NewsFileConstant.JapaneseNewsFileDir+File.separator+type
                +File.separator;
        if(newsTime==null||newsTime.equals("")){
            //默认当前系统时间
            String systemTime = CommonUtils.getSystemTime();
            String filePath = dirPath+(systemTime+".json");
            if(!FileUtil.exist(filePath)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部不存在这个日期的文件"+filePath);
            }
            translatedNewsVo = getTranslatedNewsVo(newsId,filePath,translatedNewsVo,transApi);
        }
        else{
            //根据传入时间进行查找
            String filePath = dirPath+(newsTime+".json");
            if(!FileUtil.exist(filePath)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部不存在这个日期的文件"+filePath);
            }
            translatedNewsVo = getTranslatedNewsVo(newsId,filePath,translatedNewsVo,transApi);
        }
        return translatedNewsVo;
    }


    public TranslatedNewsVo getTranslatedNewsVo(int newsId,String path,TranslatedNewsVo translatedNewsVo,TransApi transApi){
        List<News> newsList = CommonUtils.readJsonFile(path);
        if(newsList.size()==0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"读取文件错误");
        }
        News news = newsList.get(newsId);
        String transTitle = TranslateUtil.getTranslateResult(news.getNewsTitle());
        String transContent = TranslateUtil.getTranslateResult(news.getNewsContent());
        translatedNewsVo.setNewsTime(news.getNewsTime());
        translatedNewsVo.setNewsTitle(transTitle);
        translatedNewsVo.setNewsContent(transContent);
        return translatedNewsVo;
    }
}
