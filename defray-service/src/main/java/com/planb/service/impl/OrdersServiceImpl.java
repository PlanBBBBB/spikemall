package com.planb.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.planb.client.RepertoryClient;
import com.planb.client.UserClient;
import com.planb.common.Result;
import com.planb.entity.Orders;
import com.planb.service.OrdersService;
import com.planb.mapper.OrdersMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author 86139
 * @description 针对表【orders】的数据库操作Service实现
 * @createDate 2023-04-15 19:32:18
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private RepertoryClient repertoryClient;

    @Resource
    private UserClient userClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @GlobalTransactional
    public Result pay(String jwt, Long orderId) {
        String key = "order:" + orderId;
        String orderJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(orderJson)) {
            return Result.fail("订单号有误");
        }
        //获得order对象
        Orders order = JSONUtil.toBean(orderJson, Orders.class);

        //下单时间超时
        LocalDateTime deadlineTime = order.getOrderTime().plusMinutes(30);
        LocalDateTime nowTime = LocalDateTime.now();
        if (nowTime.isAfter(deadlineTime)) {
            //回滚库存
            repertoryClient.rollbackStock(order.getGoodId(), jwt);
            //删除订单
            removeById(order.getId());
            return Result.fail("下单时间超时");
        }

        //余额不足
        Long money = userClient.getMoney(jwt);

        Long price = order.getAmount();
        if (money < price) {
            //回滚库存
            repertoryClient.rollbackStock(order.getGoodId(), jwt);
            //删除订单
            removeById(order.getId());
            return Result.fail("余额不足，无法购买");
        }

        //条件成功，进行支付
        //1.更改订单状态
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getId, order.getId())
                .set(Orders::getCheckTime, nowTime)
                .set(Orders::getStatus, 1);
        update(wrapper);

        //2.扣减用户余额
        long lastMoney = money - price;
        userClient.reduceMoney(lastMoney, jwt);

        //返回订单id
        return Result.ok(order.getId());
    }
}




