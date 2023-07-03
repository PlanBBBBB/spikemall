package com.itheima.client.fallback;

import com.itheima.client.UserClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable throwable) {
        return new UserClient() {
            @Override
            public Long getMoney(String jwt) {
                log.error("获取用户余额失败", throwable);
                return null;
            }

            @Override
            public void reduceMoney(Long lastMoney, String jwt) {
                log.error("扣减用户余额失败", throwable);
            }
        };
    }
}
