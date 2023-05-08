package com.itheima.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("stockservice")
public interface StockClient {

    @GetMapping("/stock/{id}")
    boolean reduceStock(@PathVariable("id") Long goodsId, @RequestHeader("Authorization") String jwt);

}
