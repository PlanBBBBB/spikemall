package com.planb.client.fallback;

import com.planb.client.GoodClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoodClientFallbackFactory implements FallbackFactory<GoodClient> {
    @Override
    public GoodClient create(Throwable throwable) {
        return new GoodClient() {
            @Override
            public Long getPrice(Long goodsId, String jwt) {
                log.error("获取商品价格失败", throwable);
                return -1L;
            }
        };
    }
}
