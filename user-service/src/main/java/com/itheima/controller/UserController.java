package com.itheima.controller;


import com.itheima.common.Result;
import com.itheima.entity.Users;
import com.itheima.service.UsersService;
import com.itheima.utils.UserToken;
import lombok.extern.slf4j.Slf4j;
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
     * 校验
     *
     * @return
     */
    @GetMapping("/check")
    public Result check(HttpServletRequest request) {
        String token = UserToken.getToken(request);
        return usersService.check(token);
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = UserToken.getToken(request);
        return usersService.logout(token);
    }

    /**
     * 获取用户余额（对外不开放）
     *
     * @return
     */
    @GetMapping("/money/{token}")
    public Long getMoney(@PathVariable("token") String token) {
        return usersService.getMoney(token);
    }

    /**
     * 扣减用户余额（对外不开放）
     *
     * @param lastMoney
     */
    @GetMapping("/reduce/{token}/{id}")
    public void reduceMoney(@PathVariable("token") String token, @PathVariable("id") Long lastMoney) {
        usersService.reduceMoney(token, lastMoney);
    }

}
