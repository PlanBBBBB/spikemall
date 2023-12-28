package com.planb.exception;

import com.planb.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebException {

    @ExceptionHandler(Exception.class)
    public Result handleRuntimeException(RuntimeException e) {
        return Result.fail("出故障了!");
    }
}