package com.planb.client;

import com.planb.client.fallback.GoodClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "goodsservice", fallbackFactory = GoodClientFallbackFactory.class)
public interface GoodClient {

    @GetMapping("/good/get/{id}")
    Long getPrice(@PathVariable("id") Long goodsId, @RequestHeader("Authorization") String jwt);

}
