package com.codeLife.yapiToJava.entity.resp;

import lombok.Data;

import java.util.List;

/**
 * 基础返回类
 */
@Data
public class BaseResp {
    /**
     * 接口文档中的请求参数
     */
    private List<String> requestJavaObject;
    /**
     * 接口文档中的返回参数
     */
    private List<String> responseJavaObject;
}
