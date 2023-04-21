package com.itheima.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("orderservice")
public interface OrderClient {

    @GetMapping("/order/find/{userId}/{id}")
    int findCount(@PathVariable("userId") Long userId, @PathVariable("id") Long goodsId);

}
