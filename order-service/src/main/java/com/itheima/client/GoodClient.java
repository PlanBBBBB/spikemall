package com.itheima.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("goodsservice")
public interface GoodClient {

    @GetMapping("/good/{id}")
    Long getPrice(@PathVariable("id") Long goodsId);

}
