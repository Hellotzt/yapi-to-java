package com.codeLife.yapiToJava.api;

import com.codeLife.yapiToJava.common.param.Result;
import com.codeLife.yapiToJava.entity.req.ToJavaReq;
import com.codeLife.yapiToJava.entity.req.ToApiReq;
import com.codeLife.yapiToJava.entity.resp.BaseResp;
import com.codeLife.yapiToJava.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 基础控制器
 *
 * @author Hellotzt
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class BaseApi {
    private final BaseService baseService;

    /**
     * yapi链接转Java对象
     * @param toJavaReq 请求参数
     * @return Java对象
     */
    @PostMapping("/toJavaObject")
    public Result<BaseResp> toJavaObject(@Valid @RequestBody ToJavaReq toJavaReq){
        return baseService.toJavaObject(toJavaReq);
    }

    /**
     * Java转Yapi的Json格式
     * @param toApiReq 请求参数
     * @return Json参数
     */
    @PostMapping("/toApiObject")
    public Result<String> toApiObject(@Valid @RequestBody ToApiReq toApiReq){
        return baseService.toApiObject(toApiReq);
    }
}
