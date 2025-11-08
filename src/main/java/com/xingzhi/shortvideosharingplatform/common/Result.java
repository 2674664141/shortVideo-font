package com.xingzhi.shortvideosharingplatform.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
    统一全局数据类，作为返回给前端的数据类，返回前端的数据必须用这个类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer code;

    private T data;

    private String message;

    public static <T> Result<T> success(T data, String message) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.data = data;
        r.message = message;
        return r;
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.data = data;
        r.message = "请求成功";
        return r;
    }

    public static <T> Result<T> error(T data, Integer code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.data = data;
        r.message = message;
        return r;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.data = null;
        r.message = message;
        return r;
    }
}
