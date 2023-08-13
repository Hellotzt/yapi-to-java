package com.codeLife.yapiToJava.entity.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 转换yapi格式入参
 */
@Data
public class ToApiReq {
    /**
     * 类文本
     */
    @NotBlank(message = "需转换的类信息不能为空")
    private String classText;
}
