package com.itheima.utils.Intercepter;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (token == null) {
            response.setStatus(401);
            return false;
        }
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isBlank(userJson)){
            response.setStatus(401);
            return false;
        }
        return true;
    }

}