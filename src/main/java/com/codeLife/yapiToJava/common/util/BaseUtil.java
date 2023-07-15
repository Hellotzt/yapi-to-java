package com.codeLife.yapiToJava.common.util;

import com.alibaba.fastjson2.JSONObject;
import com.codeLife.yapiToJava.common.constnt.ParamConstant;

/**
 * 基础工具类
 *
 * @author Hellotzt
 */
public class BaseUtil {

    /**
     * 字符串是否包含特殊符号，如果是 截取特殊符号前面的字符串
     * @param str 字符串
     * @return 处理后的字符串
     */
    public static String splitSpecialSymbol(String str) {
        for (String specialSymbol : ParamConstant.SPECIAL_SYMBOL_LIST) {
            if (str.contains(specialSymbol)) {
                str = str.split(specialSymbol)[0];
            }
        }
        return str;
    }

    /**
     * 获取接口的方法名
     *
     * @param jsonData 请求数据
     * @return 方法名
     */
    public static String getApiPathMethod(JSONObject jsonData) {
        String apiPath = jsonData.getJSONObject("query_path").getString("path");
        String method = apiPath.split("/")[apiPath.split("/").length - 1];
        // 方法首字母大写
        return upperCase(method);
    }

    /**
     * 首字母大写
     * @param str 字符串
     * @return 处理完成的字符串
     */
    public static String upperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 获取url最后一个/后面的id
     *
     * @param url url
     * @return 请求地址的id
     */
    public static String getUrlId(String url) {
        return url.split("/")[url.split("/").length - 1];
    }

    /**
     * 获取域名
     *
     * @param url url
     * @return 域名
     */
    public static String getDomain(String url) {
        String[] split = url.split("/");
        return split[0] + "//" + split[2];
    }
}
