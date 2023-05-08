package com.itheima.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/user/money")
    Long getMoney(@RequestHeader("Authorization") String jwt);

    @GetMapping("/user/reduce/{lastMoney}")
    void reduceMoney(@PathVariable("lastMoney") Long lastMoney, @RequestHeader("Authorization") String jwt);

}
