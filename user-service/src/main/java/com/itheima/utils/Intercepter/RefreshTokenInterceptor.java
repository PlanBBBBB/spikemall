package com.itheima.utils.Intercepter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * token刷新拦截器
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {
    StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取携带在请求头中的token
        String token = request.getHeader("authorization");
        if (token==null){
            return true;
        }
        //2.从redis中找出tokenKey
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        if (userJson==null){
            return true;
        }
        //3.刷新token的时长
        stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        String token = request.getHeader("authorization");
//        String tokenKey = "login:user" + token;
//        stringRedisTemplate.delete(tokenKey);
    }
}