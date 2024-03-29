package com.planb.controller;


import com.planb.common.Result;
import com.planb.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/defray")
@Slf4j
public class DefrayController {

    @Resource
    private OrdersService ordersService;

    /**
     * 支付功能,没登录不能支付，即请求头必须携带Authorization
     *
     * @return
     */
    @PostMapping("/{orderId}")
    public Result pay(HttpServletRequest request, @PathVariable("orderId") Long orderId) {
        String jwt = request.getHeader("Authorization");
        return ordersService.pay(jwt, orderId);
    }

}