package com.planb.client;

import com.planb.client.fallback.StockClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "stockservice", fallbackFactory = StockClientFallbackFactory.class)
public interface StockClient {

    @GetMapping("/stock/{id}")
    boolean reduceStock(@PathVariable("id") Long goodsId, @RequestHeader("Authorization") String jwt);

}
