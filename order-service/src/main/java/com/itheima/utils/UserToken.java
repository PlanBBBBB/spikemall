package com.itheima.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从请求中获取token
 */
public class UserToken {

    public static String getToken(HttpServletRequest request) {
        return request.getHeader("authorization");
    }
}
