package com.itheima.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("stockservice")
public interface RepertoryClient {

    @PostMapping("/stock/{id}")
    void rollbackStock(@PathVariable("id") Long goodsId);

}
