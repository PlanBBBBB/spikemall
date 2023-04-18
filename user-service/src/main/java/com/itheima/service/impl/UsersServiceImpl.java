package com.itheima.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.Result;
import com.itheima.entity.Users;
import com.itheima.service.UsersService;
import com.itheima.mapper.UsersMapper;
import com.itheima.utils.Regex.RegexUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 86139
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2023-04-15 15:55:14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(Users user) {
        String phone = user.getPhone();
        String password = user.getPassword();
        if (RegexUtils.isPhoneInvalid(phone)) {
            //手机号格式错误
            return Result.fail("手机号格式不正确");
        }
        if (RegexUtils.isPasswordInvalid(password)) {
            //密码格式错误
            return Result.fail("密码格式不正确");
        }
        Users QueryUser = query().eq("phone", phone).one();
        if (QueryUser != null && !QueryUser.getPassword().equals(password)) {
            //存在用户，密码错误退出
            return Result.fail("密码错误，请重试!");
        }
        //不存在则新增用户
        if (QueryUser == null) {
            QueryUser = createUser(user);
        }

        //5.将用户信息存入redis
        String token = UUID.randomUUID(true).toString(true);
        String tokenKey = "login:user" + token;

        String userJson = JSONUtil.toJsonStr(QueryUser);
        stringRedisTemplate.opsForValue().set(tokenKey, userJson);

        //设置有效期
        stringRedisTemplate.expire(tokenKey, 30, TimeUnit.MINUTES);
        return Result.ok(token);
    }


    /**
     * 不存在该用户的时候自动创建用户
     *
     * @param user
     * @return
     */
    private Users createUser(Users user) {
        String name = user.getName();
        String avatar = user.getAvatar();

        Users newUser = new Users();
        newUser.setPhone(user.getPhone());
        newUser.setPassword(user.getPassword());
        newUser.setMoney(200L);//新用户送200
        if (user.getName() == null) {
            newUser.setName("user_" + UUID.randomUUID(true).toString());
        } else {
            newUser.setName(name);
        }
        if (avatar != null) {
            newUser.setAvatar(avatar);
        }
        save(newUser);
        return newUser;
    }

    /**
     * 请求头带上authorization携带的token
     *
     * @return
     */
    @Override
    public Result check(String token) {
        //从缓存中获取user对象
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);
        return Result.ok(user);
    }

    /**
     * 请求头带上authorization携带的token
     *
     * @return
     */
    @Override
    public Result logout(String token) {
        //删除缓存
        String tokenKey = "login:user" + token;
        stringRedisTemplate.delete(tokenKey);
        return Result.ok();
    }


    @Override
    public Long getMoney(String token) {
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);
        return user.getMoney();
    }

    @Override
    public void reduceMoney(String token, Long lastMoney) {
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);
        Long userId = user.getId();
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, userId)
                .set(Users::getMoney, lastMoney);
        update(updateWrapper);
    }
}




