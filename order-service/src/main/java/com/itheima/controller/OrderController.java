package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.service.OrdersService;
import com.itheima.utils.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private OrdersService ordersService;

    /**
     * 通过查询订单查看该用户是否购买过该商品，实现一人一单（对外不开放）
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/find/{token}/{id}")
    public int findCount(@PathVariable("token") String token, @PathVariable("id") Long goodsId) {
        return ordersService.findCount(token, goodsId);
    }


    /**
     * 查看当前用户的所有订单,请求头必须携带token
     *
     * @return
     */
    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        String token = UserToken.getToken(request);
        return ordersService.listByUser(token);
    }

}
