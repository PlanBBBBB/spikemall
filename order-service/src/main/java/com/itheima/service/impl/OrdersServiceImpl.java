package com.itheima.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.client.GoodClient;
import com.itheima.common.Result;
import com.itheima.entity.Orders;
import com.itheima.service.OrdersService;
import com.itheima.mapper.OrdersMapper;
import com.itheima.utils.UserToken;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private GoodClient goodClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional
    public int findCount(Long userId, Long goodsId) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getGoodId, goodsId).eq(Orders::getUserId, userId);
        return count(queryWrapper);
    }

    @Override
    @Transactional
    public void saveOrder(String jwt, Long goodsId, Long orderId) {
        Long userId;
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Long price = goodClient.getPrice(goodsId, jwt);

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
    @Transactional
    public Result listByUser(Long userId) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        List<Orders> ordersList = list(queryWrapper);
        return Result.ok(ordersList);
    }
}