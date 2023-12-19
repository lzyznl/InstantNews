package com.lzy.serverproject.model.dto;

import lombok.Data;

@Data
public class SubscribeRequest {
    private String phoneNumber;
    private Integer newsType;
    private Integer subscribe;
}
