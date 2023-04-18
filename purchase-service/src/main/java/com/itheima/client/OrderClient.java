package com.itheima.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("orderservice")
public interface OrderClient {

    @GetMapping("/order/find/{token}/{id}")
    int findCount(@PathVariable("token") String token, @PathVariable("id") Long goodsId);

}
