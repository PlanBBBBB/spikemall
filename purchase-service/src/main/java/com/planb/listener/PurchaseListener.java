package com.planb.listener;

import com.planb.service.RepertoryService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "Purchase", consumerGroup = "purchase_consumer")
public class PurchaseListener implements RocketMQListener<String> {

    @Resource
    private RepertoryService repertoryService;

    @Override
    public void onMessage(String message) {
        String[] split = message.split("=");
        String jwt = split[0];
        Long goodsId = Long.valueOf(split[1]);
        repertoryService.afterPurchase(jwt,goodsId);
    }
}
