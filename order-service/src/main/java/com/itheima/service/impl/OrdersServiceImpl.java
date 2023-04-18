package com.itheima.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.client.GoodClient;
import com.itheima.common.Result;
import com.itheima.entity.Orders;
import com.itheima.entity.Users;
import com.itheima.service.OrdersService;
import com.itheima.mapper.OrdersMapper;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 86139
 * @description 针对表【orders】的数据库操作Service实现
 * @createDate 2023-04-15 17:25:23
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GoodClient goodClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public int findCount(String token, Long goodsId) {
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);

        Long userId = user.getId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getGoodId, goodsId).eq(Orders::getUserId, userId);
        return count(queryWrapper);
    }

    @Override
    public void saveOrder(String token, Long goodsId, Long orderId) {
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);

        Long price = goodClient.getPrice(goodsId);
        Long userId = user.getId();

        //创建订单
        Orders order = new Orders();
        order.setId(orderId);
        order.setGoodId(goodsId);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(0);//  0表示未支付
        order.setUserId(userId);
        order.setAmount(price);
        save(order);

        //发送延迟消息
        String orderJson = JSONUtil.toJsonStr(order);
        String topic = "Pay";
        Message<String> message = MessageBuilder.withPayload(orderJson)
                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, "3") // 设置延迟等级为3，即10秒
                .build();
        rocketMQTemplate.send(topic, message);
    }

    @Override
    public Result listByUser(String token) {
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);
        Long userId = user.getId();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        List<Orders> ordersList = list(queryWrapper);
        return Result.ok(ordersList);
    }
}




