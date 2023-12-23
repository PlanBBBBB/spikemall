package com.itheima.client.fallback;

import com.itheima.client.StockClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockClientFallbackFactory implements FallbackFactory<StockClient> {
    @Override
    public StockClient create(Throwable throwable) {
        return new StockClient() {
            @Override
            public boolean reduceStock(Long goodsId, String jwt) {
                log.error("扣减库存失败", throwable);
                return false;
            }
        };
    }
}
