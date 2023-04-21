package com.itheima.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.Result;
import com.itheima.entity.LoginUser;
import com.itheima.entity.Users;
import com.itheima.service.UsersService;
import com.itheima.mapper.UsersMapper;
import com.itheima.utils.JwtUtil;
import com.itheima.utils.Regex.RegexUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 86139
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2023-04-15 15:55:14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService, UserDetailsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AuthenticationManager authenticationManager;


    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getPhone, phone);
        Users user = getOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("出错了，用户不存在");
        }
        //根据用户查询权限信息 添加到LoginUser中
        String power = user.getPower();
        List<String> list = new ArrayList<>();
        list.add(power);
        return new LoginUser(user, list);
    }

    @Override
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

//    @Override
//    public Result login(Users user) {
//        String phone = user.getPhone();
//        String password = user.getPassword();
//        if (RegexUtils.isPhoneInvalid(phone)) {
//            //手机号格式错误
//            return Result.fail("手机号格式不正确");
//        }
//        if (RegexUtils.isPasswordInvalid(password)) {
//            //密码格式错误
//            return Result.fail("密码格式不正确");
//        }
//        Users QueryUser = query().eq("phone", phone).one();
//        if (QueryUser != null && !QueryUser.getPassword().equals(password)) {
//            //存在用户，密码错误退出
//            return Result.fail("密码错误，请重试!");
//        }
//        //不存在则退出
//        if (QueryUser == null) {
//            return Result.fail("用户不存在");
//        }
//
//        //将用户信息存入redis
//        String token = UUID.randomUUID(true).toString(true);
//        String tokenKey = "login:user" + token;
//
//        String userJson = JSONUtil.toJsonStr(QueryUser);
//        stringRedisTemplate.opsForValue().set(tokenKey, userJson);
//
//        //设置有效期
//        stringRedisTemplate.expire(tokenKey, 30, TimeUnit.MINUTES);
//        return Result.ok(token);
//    }


    @Override
    public Result login(Users user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getPhone(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("手机号或密码错误");
        }
        //使用userId生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        user = loginUser.getUser();
        String userId = user.getId().toString();
        String jwt = JwtUtil.createJWT(userId);

        String loginUserJson = JSONUtil.toJsonStr(loginUser);
        //authenticate存入redis
        stringRedisTemplate.opsForValue().set("login:" + userId, loginUserJson);

        //把token响应给前端
        return Result.ok(jwt);
    }

    @Override
    public Result logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        stringRedisTemplate.delete("login:" + userId);
        return Result.ok("登出成功");
    }

//    /**
//     * 请求头带上authorization携带的token
//     *
//     * @return
//     */
//    @Override
//    public Result logout(String token) {
//        //删除缓存
//        String tokenKey = "login:user" + token;
//        stringRedisTemplate.delete(tokenKey);
//        return Result.ok();
//    }


    @Override
    public Long getMoney(Long userId) {
        Users user = getById(userId);
        return user.getMoney();
    }

    @Override
    public void reduceMoney(Long userId, Long lastMoney) {
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, userId)
                .set(Users::getMoney, lastMoney);
        update(updateWrapper);
    }
}




