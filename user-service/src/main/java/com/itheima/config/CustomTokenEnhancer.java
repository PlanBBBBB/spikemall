package com.itheima.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomTokenEnhancer implements TokenEnhancer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        String userId = stringRedisTemplate.opsForValue().get("login");
        stringRedisTemplate.delete("login");
//        String userId = authentication.getName(); // 获取用户ID
        additionalInfo.put("user_id", userId);

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}