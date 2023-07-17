package com.codeLife.yapiToJava.common.exception;

import com.codeLife.yapiToJava.common.param.CodeMsg;
import com.codeLife.yapiToJava.common.param.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result<Object> other(CustomException e) {
        log.error("自定义异常打印：{} ",e.getMessage(),e);
        return Result.fail(new CodeMsg(e.getCode(), e.getMsg()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Object> other(Exception e) {
        log.error("全局异常打印：{} ",e.getMessage(),e);
        return Result.fail(CodeMsg.SERVER_ERROR);
    }
}