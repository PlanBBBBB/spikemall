package com.itheima.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/user/money/{userId}")
    Long getMoney(@PathVariable("userId") Long userId);

    @GetMapping("/user/reduce/{userId}/{lastMoney}")
    void reduceMoney(@PathVariable("userId") Long userId, @PathVariable("lastMoney") Long lastMoney);

}
