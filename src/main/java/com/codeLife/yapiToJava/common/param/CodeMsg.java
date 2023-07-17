package com.codeLife.yapiToJava.common.param;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * 通用返回码配置
 */
@Data
public class CodeMsg {

    /**
     * 通用的返回码
     */
    public static CodeMsg SUCCESS = new CodeMsg("000000", "操作成功");
    public static CodeMsg FAIL = new CodeMsg("500000", "操作失败");

    public static CodeMsg SERVER_ERROR = new CodeMsg("999999", "系统错误,请联系管理员");

    private String code;
    private String msg;
    private String timestamp;

    public CodeMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss");
    }
}
