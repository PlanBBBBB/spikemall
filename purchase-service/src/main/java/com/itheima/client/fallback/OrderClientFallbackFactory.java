package com.itheima.client.fallback;

import com.itheima.client.OrderClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable throwable) {
        return new OrderClient() {
            @Override
            public int findCount(Long goodsId, String jwt) {
                log.error("查询订单数量失败",throwable);
                return 0;
            }
        };
    }
}
