package com.planb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planb.entity.LoginUser;
import com.planb.entity.Users;
import com.planb.mapper.UsersMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getPhone, phone);
        Users user = usersMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("出错了，用户不存在");
        }
        stringRedisTemplate.opsForValue().set("login", String.valueOf(user.getId()), 30, TimeUnit.MINUTES);
        //根据用户查询权限信息 添加到LoginUser中
        String power = user.getPower();
        List<String> list = new ArrayList<>();
        list.add(power);
        return new LoginUser(user, list);
    }
}
