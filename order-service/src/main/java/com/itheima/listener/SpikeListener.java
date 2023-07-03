package com.itheima.listener;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.entity.Orders;
import com.itheima.service.OrdersService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "Order", consumerGroup = "order_consumer")
public class SpikeListener implements RocketMQListener<String> {

    @Resource
    private OrdersService ordersService;

    @Override
    public void onMessage(String message) {
        String[] split = message.split("=");
        String jwt = split[0];
        Long goodsId = Long.valueOf(split[1]);
        Long orderId = Long.valueOf(split[2]);
        ordersService.saveOrder(jwt, goodsId, orderId);
    }
}


//@Component
//@RocketMQTransactionListener
//public class SpikeListener implements RocketMQLocalTransactionListener {
//
//    @Resource
//    private OrdersService ordersService;
//
//    @Override
//    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
//        String[] split = new String((byte[]) message.getPayload()).split("=");
//        String jwt = split[0];
//        Long goodsId = Long.valueOf(split[1]);
//        Long orderId = Long.valueOf(split[2]);
//        ordersService.saveOrder(jwt, goodsId, orderId);
//        return RocketMQLocalTransactionState.COMMIT;
//    }
//
//    @Override
//    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
//        String[] split = new String((byte[]) message.getPayload()).split("=");
//        Long orderId = Long.valueOf(split[2]);
//        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Orders::getId, orderId);
//        int count = ordersService.count(wrapper);
//        if (count != 1) {
//            return RocketMQLocalTransactionState.ROLLBACK;
//        }
//        return RocketMQLocalTransactionState.COMMIT;
//    }
//}