package com.lzy.serverproject.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class NewsDataVo {
    private Map<String, List<DayTimeVo>> dayMap;
    private Map<String,List<WeekTimeVo>> weekMap;
    private Map<String,List<MonthTimeVo>> monthMap;
}
