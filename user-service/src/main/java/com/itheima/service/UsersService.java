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

    Long getMoney(Long userId);

    void reduceMoney(Long userId, Long lastMoney);

//    Result login(Users user);
//
//    Result logout();

    Result register(Users user);
}
