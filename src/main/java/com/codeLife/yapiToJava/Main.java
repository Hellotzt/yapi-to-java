package com.codeLife.yapiToJava;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.codeLife.yapiToJava.constnt.ParamConstant;
import com.codeLife.yapiToJava.enums.JavaTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * yapi转java实体类
 *
 * @author Hellotzt
 */
public class Main {
    // 复制要生成的接口路径
    public static String apiUrl = "http://xxx/project/943/interface/api/49762";
    // 复制自己的cookie
    public static String cookieValue = "_yapi_token=eyJhbGciOiJIUzI1NiIsInRNT" +
            "gyLCJleHAiOjE2ODk2NjEzODJ9.TY0gXuIgfRe_8o2tXhwFuPoqdvvm3y_zYRlOeALCBvA; _yapi_uid=541";
    public static List<String> subClassList = new ArrayList<>();
    public static void main(String[] args) {
        String requestUrl = getDomain(apiUrl) + "/api/interface/get?id=" + getUrlId(apiUrl);
        String result = HttpRequest.get(requestUrl)
                .header(ParamConstant.COOKIE, cookieValue)
                .execute().body();
        JSONObject jsonData = JSON.parseObject(result).getJSONObject(ParamConstant.DATA);
        System.out.println(jsonData);
        System.out.println("-----");
        requestParamToJava(jsonData);
        responseParamToJava(jsonData);
    }

    private static void responseParamToJava(JSONObject jsonData) {
        System.out.println("以下为返回参数所对应的java实体类----------------");
        String apiPathMethodName = getApiPathMethod(jsonData);
        JSONObject reqBodyOther = jsonData.getJSONObject("res_body");
        // System.out.println(reqBodyOther);
        // System.out.println("-----");
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther,ParamConstant.VO);
        System.out.println(aClass);

        System.out.println("下面是参数接收对象：-------------------" );
        subClassList.forEach(System.out::println);
    }

    /**
     * 获取请求参数所对应的java实体类
     *
     * @param jsonData json数据
     */
    private static void requestParamToJava(JSONObject jsonData) {
        System.out.println("以下为请求参数所对应的java实体类----------------");
        String apiPathMethodName = getApiPathMethod(jsonData);
        JSONObject reqBodyOther = jsonData.getJSONObject("req_body_other");
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther,ParamConstant.DTO);
        System.out.println(aClass);

        System.out.println("下面是参数接收对象：-------------------" );
        subClassList.forEach(System.out::println);
    }

    private static String buildClass(String classDescription, String apiPathMethodName, JSONObject reqBodyOther,String paramType) {
        JSONArray requiredArray = reqBodyOther.getJSONArray("required");
        JSONObject properties;
        if (JavaTypeEnum.Array.getYapi().equals(reqBodyOther.getString("type"))){
            properties = reqBodyOther.getJSONObject("type").getJSONObject("properties");
        }else {
            properties = reqBodyOther.getJSONObject("properties");
        }

        String subclass;
        StringBuilder classValue = new StringBuilder();
        classValue.append("/**\n").append("* ").append(classDescription).append(paramType).append("\n*/\n");
        classValue.append(ParamConstant.CLASS).append(apiPathMethodName).append(paramType).append("{\n");
        for (String key : properties.keySet()) {
            JSONObject keyJson = properties.getJSONObject(key);
            String description = keyJson.getString(ParamConstant.DESCRIPTION);
            if (StrUtil.isBlank(description)){
                description = key;
            }

            String apiType = keyJson.getString("type");
            classValue.append("\t/**\n").append("\t* ").append(description).append("\n\t*/\n");
            if (requiredArray != null && (requiredArray.contains(key)) && paramType.equals(ParamConstant.DTO)) {

                String verifyType = JavaTypeEnum.typeToVerify(apiType);
                classValue.append("\t").append(verifyType);
                if (!JavaTypeEnum.Object.getVerify().equals(verifyType)){
                    // 移除特殊符号后的注释
                    description = splitSpecialSymbol(description);
                    classValue.append("(message = \"").append(description).append("不能为空\")\n");
                }else {
                    classValue.append("\n");
                }
            }
            String javaType = JavaTypeEnum.typeToJava(apiType);
            classValue.append(ParamConstant.STATS_PREFIX);
            // 如果是数组类型，需要加上泛型
            if (JavaTypeEnum.Array.getJava().equals(javaType)) {
                String type = keyJson.getJSONObject("items").getString("type");
                if (JavaTypeEnum.Object.getYapi().equals(type)) {

                    String className;
                    if (paramType.equals(ParamConstant.VO)) {
                        className = apiPathMethodName;
                        description = classDescription;
                    }else {
                        className = upperCase(key) + paramType;
                    }

                    // classValue.append(className);
                    subclass = buildClass(description, className, keyJson,paramType);
                    subClassList.add(subclass);
                    classValue.append(javaType).append("<")
                            .append(className)
                            .append(">");
                }else {
                    classValue.append(javaType).append("<")
                            .append(JavaTypeEnum.typeToJava(type))
                            .append(">");
                }

            } else if (JavaTypeEnum.Object.getJava().equals(javaType)) {
                String className;
                if (paramType.equals(ParamConstant.VO)) {

                    className = apiPathMethodName;
                    description = classDescription;
                    if (JavaTypeEnum.Object.getYapi().equals(keyJson.getString("type"))){
                        description = keyJson.getString(ParamConstant.DESCRIPTION);
                        className = upperCase(key);
                    }
                }else {
                    className = upperCase(key);
                }
                classValue.append(className).append(paramType);
                subclass = buildClass(description, className, keyJson,paramType);
                subClassList.add(subclass);
            }else {
                classValue.append(javaType);
            }
            classValue.append(" ").append(key).append(";\n");
        }
        return classValue + "}";

    }

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
    private static String upperCase(String str) {
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