package com.codeLife.yapiToJava.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.codeLife.yapiToJava.common.constnt.ParamConstant;
import com.codeLife.yapiToJava.common.enums.JavaTypeEnum;
import com.codeLife.yapiToJava.common.exception.CustomException;
import com.codeLife.yapiToJava.common.param.CodeMsg;
import com.codeLife.yapiToJava.common.param.Result;
import com.codeLife.yapiToJava.common.util.BaseUtil;
import com.codeLife.yapiToJava.entity.req.BaseReq;
import com.codeLife.yapiToJava.entity.resp.BaseResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础实现类
 *
 * @author Hellotzt
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseService {

    public Result<BaseResp> toJavaObject(BaseReq baseReq) {
        String requestUrl = BaseUtil.getDomain(baseReq.getApiUrl()) + "/api/interface/get?id=" + BaseUtil.getUrlId(baseReq.getApiUrl());
        String result = HttpRequest.get(requestUrl)
                .header(ParamConstant.COOKIE, baseReq.getCookie())
                .execute().body();
        JSONObject jsonObject = JSON.parseObject(result);
        if (!jsonObject.get("errcode").equals(0)){
            throw new CustomException(new CodeMsg("50000",jsonObject.getString("errmsg"))) ;
        }
        JSONObject jsonData = jsonObject.getJSONObject(ParamConstant.DATA);
        System.out.println(jsonData);
        System.out.println("-----");
        List<String> reqList = requestParamToJava(jsonData);
        List<String> respList = responseParamToJava(jsonData);
        BaseResp baseResp = new BaseResp();
        baseResp.setRequestJavaObject(reqList);
        baseResp.setResponseJavaObject(respList);
        return Result.success(baseResp);
    }

    private static List<String> responseParamToJava(JSONObject jsonData) {
        System.out.println("以下为返回参数所对应的java实体类----------------");
        String apiPathMethodName = BaseUtil.getApiPathMethod(jsonData);
        JSONObject reqBodyOther = jsonData.getJSONObject("res_body");
        // System.out.println(reqBodyOther);
        // System.out.println("-----");
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        List<String> subClassList = new ArrayList<>();
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther,ParamConstant.VO,subClassList);
        System.out.println(aClass);
        subClassList.add(0,aClass);
        return subClassList;
        // System.out.println("下面是参数接收对象：-------------------" );
        // for (StringBuilder str : subClassList) {
        //     aClass.append("\n").append(str);
        // }
        // return aClass.toString();
    }

    /**
     * 获取请求参数所对应的java实体类
     *
     * @param jsonData json数据
     */
    private static List<String> requestParamToJava(JSONObject jsonData) {
        System.out.println("以下为请求参数所对应的java实体类----------------");
        String apiPathMethodName = BaseUtil.getApiPathMethod(jsonData);
        JSONObject reqBodyOther = jsonData.getJSONObject("req_body_other");
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        List<String> subClassList = new ArrayList<>();
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther, ParamConstant.DTO, subClassList);
        System.out.println(aClass);
        subClassList.add(0,aClass);
        return subClassList;
        // System.out.println("下面是参数接收对象：-------------------" );
        // for (StringBuilder str : subClassList) {
        //     aClass.append("\n").append(str);
        // }
        // return aClass.toString();
    }

    private static String buildClass(String classDescription, String apiPathMethodName, JSONObject reqBodyOther,
                                            String paramType,List<String> subClassList) {
        JSONArray requiredArray = reqBodyOther.getJSONArray("required");
        JSONObject properties;
        if (JavaTypeEnum.Array.getYapi().equals(reqBodyOther.getString("type"))){
            // .getJSONObject("items").
            properties = reqBodyOther.getJSONObject("items").getJSONObject("properties");
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
                    description = BaseUtil.splitSpecialSymbol(description);
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
                        className = BaseUtil.upperCase(key) + paramType;
                    }

                    // classValue.append(className);
                    subclass = buildClass(description, className, keyJson,paramType,subClassList);
                    subClassList.add(subclass);
                    classValue.append(javaType).append("<")
                            .append(className).append(ParamConstant.VO)
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
                        className = BaseUtil.upperCase(key);
                    }
                }else {
                    className = BaseUtil.upperCase(key);
                }
                classValue.append(className).append(paramType);
                subclass = buildClass(description, className, keyJson,paramType,subClassList);
                subClassList.add(subclass);
            }else {
                classValue.append(javaType);
            }
            classValue.append(" ").append(key).append(";\n");
        }
        return classValue.append("}").toString();

    }

}
