package com.codeLife.yapiToJava.common.exception;

import com.codeLife.yapiToJava.common.param.CodeMsg;
import com.codeLife.yapiToJava.common.param.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<Object> methodArgumentNotValidBizException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> allErrors = bindingResult.getFieldErrors();
        String ret = "BindException";
        if (!CollectionUtils.isEmpty(allErrors)) {
            FieldError objectError = allErrors.get(0);
            ret = "【" + objectError.getField() + "】" + objectError.getDefaultMessage();
        }
        log.error("BindException >>> {}", ret);
        return Result.fail(CodeMsg.PARAM_FAIL.fillArgs(ret));
    }
}