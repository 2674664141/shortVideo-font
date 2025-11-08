package com.xingzhi.shortvideosharingplatform.exception;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xingzhi.shortvideosharingplatform.common.Result;


@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        //打印异常信息
        e.printStackTrace();
        //返回错误结果
        return Result.error(400, StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
