package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.service.OrdersService;
import com.itheima.utils.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/find/{goodsId}")
    public int findCount(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        String jwt = request.getHeader("Authorization");
        Long userId = null;
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordersService.findCount(userId, goodsId);
    }


    /**
     * 查看当前用户的所有订单,请求头必须携带jwt
     *
     * @return
     */
    @GetMapping("/list")
    public Result listByUser(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        Long userId = null;
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordersService.listByUser(userId);
    }

}
