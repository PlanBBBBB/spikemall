package com.itheima.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/user/money/{token}")
    Long getMoney(@PathVariable("token") String token);

    @GetMapping("/user/reduce/{token}/{id}")
    void reduceMoney(@PathVariable("token") String token, @PathVariable("id") Long lastMoney);

}
