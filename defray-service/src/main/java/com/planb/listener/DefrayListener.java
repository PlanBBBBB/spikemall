package com.planb.listener;


import cn.hutool.json.JSONUtil;
import com.planb.entity.Orders;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "Pay", consumerGroup = "pay_consumer")
public class DefrayListener implements RocketMQListener<String> {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(String orderJson) {
        Orders order = JSONUtil.toBean(orderJson, Orders.class);
        String key = "order:" + order.getId();
        stringRedisTemplate.opsForValue().set(key, orderJson, 25, TimeUnit.MINUTES);
    }
}


