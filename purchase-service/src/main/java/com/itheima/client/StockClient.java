package com.itheima.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("stockservice")
public interface StockClient {

    @GetMapping("/stock/{id}")
    boolean reduceStock(@PathVariable("id") Long goodsId);

}
