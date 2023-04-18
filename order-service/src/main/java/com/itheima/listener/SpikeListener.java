package com.itheima.listener;


import com.itheima.service.OrdersService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "Order", consumerGroup = "order_consumer")
public class SpikeListener implements RocketMQListener<String> {

    @Resource
    private OrdersService ordersService;

    @Override
    public void onMessage(String message) {
//        System.out.println(message);
        String[] split = message.split("_");
        String token = split[0];
        Long goodsId = Long.valueOf(split[1]);
        Long orderId = Long.valueOf(split[2]);
        ordersService.saveOrder(token, goodsId, orderId);
    }

}
