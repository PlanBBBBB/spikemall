package com.itheima.service;

import com.itheima.common.Result;
import com.itheima.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【users】的数据库操作Service
* @createDate 2023-04-15 15:55:14
*/
public interface UsersService extends IService<Users> {
    Long getMoney(String token);

    void reduceMoney(String token, Long lastMoney);

    Result login(Users user);

    Result check(String token);

    Result logout(String token);
}
