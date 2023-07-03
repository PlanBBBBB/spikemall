package com.itheima.client;

import com.itheima.client.fallback.OrderClientFallbackFactory;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "orderservice",fallbackFactory = OrderClientFallbackFactory.class)
public interface OrderClient {

    @GetMapping("/order/find/{goodsId}")
    int findCount(@PathVariable("goodsId") Long goodsId,@RequestHeader("Authorization") String jwt);

}