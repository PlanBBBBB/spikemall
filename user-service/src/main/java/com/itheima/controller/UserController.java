package com.itheima.controller;


import com.itheima.common.Result;
import com.itheima.entity.Users;
import com.itheima.service.UsersService;
import com.itheima.utils.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UsersService usersService;


    /**
     * 注册
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody Users user) {
        return usersService.register(user);
    }


    /**
     * 获取用户余额（对外不开放）
     *
     * @return
     */
    @GetMapping("/money")
    public Long getMoney(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        Long userId = null;
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersService.getMoney(userId);
    }

    /**
     * 扣减用户余额（对外不开放）
     *
     * @param lastMoney
     */
    @GetMapping("/reduce/{lastMoney}")
    public void reduceMoney(@PathVariable("lastMoney") Long lastMoney, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        Long userId = null;
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        usersService.reduceMoney(userId, lastMoney);
    }

}