package com.itheima.utils;

import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

/**
 * 从请求中获取jwt
 */
public class UserToken {

    public static Long getToken(HttpServletRequest request) {
        String jwt = request.getHeader("authorization");
        long userId;
        try {
            Claims claims = JwtUtil.parseJWT(jwt);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("authorization非法");
        }
        return userId;
    }
}
