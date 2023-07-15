package com.codeLife.yapiToJava.entity.req;

import lombok.Data;

/**
 * 基础请求类
 */
@Data
public class BaseReq {
    /**
     * 接口路径
     */
    private String apiUrl;
    /**
     * 小甜饼
     */
    private String cookie;
}
