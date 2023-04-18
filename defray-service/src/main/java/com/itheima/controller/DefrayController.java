package com.itheima.controller;


import com.itheima.common.Result;
import com.itheima.service.OrdersService;
import com.itheima.utils.UserToken;
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
     * 支付功能,没登录不能支付，即请求头必须携带token
     *
     * @return
     */
    @PostMapping("/{orderId}")
    public Result pay(HttpServletRequest request, @PathVariable("orderId") Long orderId) {
        String token = UserToken.getToken(request);
        return ordersService.pay(token, orderId);
    }

}
