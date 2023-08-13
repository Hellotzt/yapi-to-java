package com.codeLife.yapiToJava.entity.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 转Java对象入参
 */
@Data
public class ToJavaReq {
    /**
     * 接口路径
     */
    @NotBlank(message = "接口路径不能为空")
    private String apiUrl;
    /**
     * 小甜饼
     */
    @NotBlank(message = "cookie不能为空")
    private String cookie;
}
