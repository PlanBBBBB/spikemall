package com.planb.client;

import com.planb.client.fallback.RepertoryClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "stockservice",fallbackFactory = RepertoryClientFallbackFactory.class)
public interface RepertoryClient {

    @PostMapping("/stock/{id}")
    void rollbackStock(@PathVariable("id") Long goodsId, @RequestHeader("Authorization") String jwt);

}
