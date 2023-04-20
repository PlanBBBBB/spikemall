package com.itheima.config;

import com.itheima.utils.Intercepter.LoginInterceptor;
import com.itheima.utils.Intercepter.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 做登录校验的配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate)).excludePathPatterns(
                "/user/register",
                "/user/login",
                "/user/money/**",
                "/user/reduce/**",
                "/good/**",
                "/stock/**",
                "order/find/**",
                "order/save/**"
        ).order(1);
        //token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
    }
}