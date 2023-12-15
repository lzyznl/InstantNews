package com.lzy.serverproject.model.translate;

import lombok.Data;

@Data
public class translateResult {
    private String to;
    private String from;
    private trans_result[] trans_result;
}
