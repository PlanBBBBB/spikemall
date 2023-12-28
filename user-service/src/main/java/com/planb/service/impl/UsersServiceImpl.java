package com.planb.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.planb.common.Result;
import com.planb.entity.Users;
import com.planb.service.UsersService;
import com.planb.mapper.UsersMapper;
import com.planb.utils.Regex.RegexUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 86139
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2023-04-15 15:55:14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Override
    @Transactional
    public Result register(Users user) {
        String phone = user.getPhone();
        String password = user.getPassword();
        String name = user.getName();
        String avatar = user.getAvatar();
        if (RegexUtils.isPhoneInvalid(phone)) {
            //手机号格式错误
            return Result.fail("手机号格式不正确");
        }
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getPhone, phone);
        List<Users> list = list(queryWrapper);
        if (!list.isEmpty()) {
            return Result.fail("用户已存在");
        }
        if (RegexUtils.isPasswordInvalid(password)) {
            //密码格式错误
            return Result.fail("密码格式不正确");
        }
        Users newUser = new Users();
        newUser.setPhone(user.getPhone());
        //密码设置为加密形式
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(password);
        newUser.setPassword(encode);
        newUser.setMoney(200L);//新用户送200
        newUser.setPower("consumer");
        if (user.getName() == null) {
            newUser.setName("user_" + UUID.randomUUID(true).toString());
        } else {
            newUser.setName(name);
        }
        if (avatar != null) {
            newUser.setAvatar(avatar);
        }
        save(newUser);
        return Result.ok();
    }


    @Override
    @Transactional
    public Long getMoney(Long userId) {
        Users user = getById(userId);
        return user.getMoney();
    }

    @Override
    @Transactional
    public void reduceMoney(Long userId, Long lastMoney) {
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, userId)
                .set(Users::getMoney, lastMoney);
        update(updateWrapper);
    }
}




