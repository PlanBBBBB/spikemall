package com.planb.client.fallback;

import com.planb.client.RepertoryClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RepertoryClientFallbackFactory implements FallbackFactory<RepertoryClient> {
    @Override
    public RepertoryClient create(Throwable throwable) {
        return new RepertoryClient() {
            @Override
            public void rollbackStock(Long goodsId, String jwt) {
                log.error("回滚库存失败",throwable);
            }
        };
    }
}
