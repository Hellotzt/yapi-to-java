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
import com.codeLife.yapiToJava.entity.req.ToJavaReq;
import com.codeLife.yapiToJava.entity.req.ToApiReq;
import com.codeLife.yapiToJava.entity.resp.BaseResp;
import com.codeLife.yapiToJava.entity.resp.ToApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Result<BaseResp> toJavaObject(ToJavaReq toJavaReq) {
        String requestUrl = BaseUtil.getDomain(toJavaReq.getApiUrl()) + "/api/interface/get?id=" + BaseUtil.getUrlId(toJavaReq.getApiUrl());
        String result = HttpRequest.get(requestUrl)
                .header(ParamConstant.COOKIE, toJavaReq.getCookie())
                .execute().body();
        JSONObject jsonObject = JSON.parseObject(result);
        if (!jsonObject.get("errcode").equals(0)) {
            throw new CustomException(new CodeMsg("50000", jsonObject.getString("errmsg")));
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
        JSONObject reqBodyOther = jsonData.getJSONObject(ParamConstant.RES_BODY);
        // System.out.println(reqBodyOther);
        // System.out.println("-----");
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        List<String> subClassList = new ArrayList<>();
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther, ParamConstant.VO, subClassList);
        subClassList.add(0, aClass);
        return subClassList;
    }

    /**
     * 获取请求参数所对应的java实体类
     *
     * @param jsonData json数据
     */
    private static List<String> requestParamToJava(JSONObject jsonData) {
        System.out.println("以下为请求参数所对应的java实体类----------------");
        String apiPathMethodName = BaseUtil.getApiPathMethod(jsonData);
        JSONObject reqBodyOther = jsonData.getJSONObject(ParamConstant.REQ_BODY_OTHER);
        String classDescription = jsonData.getString(ParamConstant.TITLE);
        List<String> subClassList = new ArrayList<>();
        String aClass = buildClass(classDescription, apiPathMethodName, reqBodyOther, ParamConstant.DTO, subClassList);
        subClassList.add(0, aClass);
        return subClassList;
    }

    private static String buildClass(String classDescription, String apiPathMethodName, JSONObject reqBodyOther,
                                     String paramType, List<String> subClassList) {
        JSONArray requiredArray = reqBodyOther.getJSONArray(ParamConstant.REQUIRED);
        JSONObject properties;
        if (JavaTypeEnum.Array.getYapi().equals(reqBodyOther.getString(ParamConstant.TYPE))) {
            // .getJSONObject("items").
            properties = reqBodyOther.getJSONObject(ParamConstant.ITEMS).getJSONObject(ParamConstant.PROPERTIES);
        } else {
            properties = reqBodyOther.getJSONObject(ParamConstant.PROPERTIES);
        }

        String subclass;
        StringBuilder classValue = new StringBuilder();
        classValue.append("/**\n").append("* ").append(classDescription).append(paramType).append("\n*/\n");
        classValue.append(ParamConstant.CLASS).append(apiPathMethodName).append(paramType).append("{\n");
        // 必填校验注解需要的import语句
        StringBuilder requireImportStr = new StringBuilder(ParamConstant.IMPORT_DATA);
        boolean isNeedListImport = false;
        for (String key : properties.keySet()) {
            JSONObject keyJson = properties.getJSONObject(key);
            String description = keyJson.getString(ParamConstant.DESCRIPTION);
            if (StrUtil.isBlank(description)) {
                description = key;
            }

            String apiType = keyJson.getString(ParamConstant.TYPE);
            classValue.append("\t/**\n").append("\t* ").append(description).append("\n\t*/\n");
            if (requiredArray != null && (requiredArray.contains(key)) && paramType.equals(ParamConstant.DTO)) {

                String verifyType = JavaTypeEnum.typeToVerify(apiType);
                String importStr = JavaTypeEnum.typeToImportStr(apiType);
                if (requireImportStr.indexOf(importStr) == -1) {
                    requireImportStr.append(importStr);
                }
                classValue.append("\t").append(verifyType);
                if (!JavaTypeEnum.Object.getVerify().equals(verifyType)) {
                    // 移除特殊符号后的注释
                    description = BaseUtil.splitSpecialSymbol(description);
                    classValue.append("(message = \"").append(description).append("不能为空\")\n");
                } else {
                    classValue.append("\n");
                }
            }
            String javaType = JavaTypeEnum.typeToJava(apiType);
            classValue.append(ParamConstant.STATS_PREFIX);
            // 如果是数组类型，需要加上泛型
            if (JavaTypeEnum.Array.getJava().equals(javaType)) {
                String type = keyJson.getJSONObject(ParamConstant.ITEMS).getString(ParamConstant.TYPE);
                if (JavaTypeEnum.Object.getYapi().equals(type)) {
                    isNeedListImport = true;
                    String className;
                    if (paramType.equals(ParamConstant.VO)) {
                        className = apiPathMethodName;
                        description = classDescription;
                    } else {
                        className = BaseUtil.upperCase(key) + paramType;
                    }

                    // classValue.append(className);
                    subclass = buildClass(description, className, keyJson, paramType, subClassList);
                    subClassList.add(subclass);
                    classValue.append(javaType).append("<")
                            .append(className).append(ParamConstant.VO)
                            .append(">");
                } else {
                    classValue.append(javaType).append("<")
                            .append(JavaTypeEnum.typeToJava(type))
                            .append(">");
                }

            } else if (JavaTypeEnum.Object.getJava().equals(javaType)) {
                String className;
                if (paramType.equals(ParamConstant.VO)) {

                    className = apiPathMethodName;
                    description = classDescription;
                    if (JavaTypeEnum.Object.getYapi().equals(keyJson.getString(ParamConstant.TYPE))) {
                        description = keyJson.getString(ParamConstant.DESCRIPTION);
                        className = BaseUtil.upperCase(key);
                    }
                } else {
                    className = BaseUtil.upperCase(key);
                }
                classValue.append(className).append(paramType);
                subclass = buildClass(description, className, keyJson, paramType, subClassList);
                subClassList.add(subclass);
            } else {
                classValue.append(javaType);
            }
            classValue.append(" ").append(key).append(";\n");
        }
        if (isNeedListImport) {
            requireImportStr.append(ParamConstant.LIST_IMPORT);
        }
        return requireImportStr.append("\n").append(classValue).append("}").toString();

    }

    public Result<String> toApiObject(ToApiReq toApiReq) {
        String classText = toApiReq.getClassText();
        if (!classText.contains(ParamConstant.BASE_CLASS)) {
            log.error("该请求不是一个正常的类对象");
            throw new CustomException(CodeMsg.PARAM_FAIL);
        }
        ToApi toApi = new ToApi();
        JSONObject properties = new JSONObject();
        toApi.setSchema(ParamConstant.BASE_SCHEMA);
        toApi.setType(JavaTypeEnum.Object.getYapi());
        String[] split = classText.split("\\{");
        String s = "";
        boolean b = false;
        for (String s1 : split) {
            if (s1.contains("}")){

                s = Arrays.toString(split).split("\\{")[0];
                b=true;
                break;
            }
        }
        if (b){
            s = split[split.length - 1].split("}")[0];
        }
        String[] paramArr = s.trim().split(";");
        for (String param : paramArr) {
            String description = readDescription(param);
            String[] typeArr = param.split(ParamConstant.PRIVATE);
            String type = typeArr[typeArr.length - 1].trim().split(" ")[0];
            String[] nameArr = param.split(type);
            String name = nameArr[nameArr.length - 1].trim();
            log.info("字段名： {} 类型：{} 注释：{}", name, type, description);
            JSONObject jsonObject = new JSONObject();
            if (type.startsWith(JavaTypeEnum.Array.getJava())) {
                JSONObject json = new JSONObject();
                String[] split66 = type.split("<");
                json.put(ParamConstant.TYPE, JavaTypeEnum.typeToYapi(split66[split66.length - 1].split(">")[0]));
                type = JavaTypeEnum.Array.getJava();
                jsonObject.put(ParamConstant.ITEMS, json);
            }
            jsonObject.put(ParamConstant.TYPE, JavaTypeEnum.typeToYapi(type));
            jsonObject.put(ParamConstant.DESCRIPTION, description);
            properties.put(name, jsonObject);
        }
        toApi.setProperties(properties);
        toApi.setRequired(readRequired(classText));
        return Result.success(JSON.toJSONString(toApi));
    }

    /**
     * 读取注释内容
     *
     * @param code 代码块
     * @return 注释
     */
    public static String readDescription(String code) {
        for (String line : code.split("\n")) {
            line = line.trim();
            if (line.startsWith("*")) {
                String[] split = line.split("\\*");
                return split[split.length - 1].trim();
            }
        }
        return "";
    }

    /**
     * 读取类中必填项
     *
     * @param classCode 类代码
     * @return 必填项list
     */
    private static List<String> readRequired(String classCode) {
        List<String> requiredList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(classCode))) {
            String line;
            boolean flag = false;
            while ((line = reader.readLine()) != null) {
                if (flag) {
                    if (line.contains(ParamConstant.PRIVATE)) {
                        String[] s = line.split(" ");
                        String s1 = s[s.length - 1].split(";")[0];
                        requiredList.add(s1);
                        flag = false;
                    }
                }
                for (JavaTypeEnum typeEnum : JavaTypeEnum.values()) {
                    if (line.contains(typeEnum.getVerify())) {
                        flag = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("解析行数据异常：", e);
        }
        return requiredList;
    }
}
