package com.itheima.controller;


import com.itheima.common.Result;
import com.itheima.entity.Users;
import com.itheima.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UsersService usersService;

    //TODO 超级管理员有的权限功能


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
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Users user) {
        return usersService.login(user);
    }


    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result logout() {
        return usersService.logout();
    }

    /**
     * 获取用户余额（对外不开放）
     *
     * @return
     */
    @GetMapping("/money/{userId}")
    public Long getMoney(@PathVariable("userId") Long userId) {
        return usersService.getMoney(userId);
    }

    /**
     * 扣减用户余额（对外不开放）
     *
     * @param lastMoney
     */
    @GetMapping("/reduce/{userId}/{lastMoney}")
    public void reduceMoney(@PathVariable("userId") Long userId, @PathVariable("lastMoney") Long lastMoney) {
        usersService.reduceMoney(userId, lastMoney);
    }

}