package com.codeLife.yapiToJava.api;

import com.codeLife.yapiToJava.entity.req.BaseReq;
import com.codeLife.yapiToJava.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/toJavaObject")
    public String toJavaObject(@RequestBody BaseReq baseReq){
        return baseService.toJavaObject(baseReq);
    }
}
