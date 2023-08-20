package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.client.OrderClient;
import com.itheima.client.StockClient;
import com.itheima.common.Result;
import com.itheima.entity.Repertory;
import com.itheima.service.RepertoryService;
import com.itheima.mapper.RepertoryMapper;
import com.itheima.utils.RedisIdWorker;
import com.itheima.utils.UserToken;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author 86139
 * @description 针对表【repertory】的数据库操作Service实现
 * @createDate 2023-04-15 16:26:28
 */
@Service
public class RepertoryServiceImpl extends ServiceImpl<RepertoryMapper, Repertory> implements RepertoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderClient orderClient;

    @Resource
    private StockClient stockClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    Long userId;
    long orderId;
    RepertoryService proxy;

    @Override
    @GlobalTransactional
    public Result spikeGoods(String jwt, Long goodsId) {
        //获取用户id
        try {
            userId = UserToken.getUserIdFromToken(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("解析jwt失败");
        }

        //执行lua脚本
        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(), goodsId.toString(),
                userId.toString());
        //判断返回值，并返回错误信息
        if (result.intValue() != 0) {
            return Result.fail(result.intValue() == 1 ? "库存不足" : "不能重复下单");
        }
        orderId = redisIdWorker.nextId("order");

        //防止其他线程无法获得代理对象
        proxy = (RepertoryService) AopContext.currentProxy();

        // 基于MQ队列实现异步
        String topic = "Purchase";
        String content = jwt + "=" + goodsId;
        rocketMQTemplate.convertAndSend(topic, content);

        return Result.ok(orderId);
    }


    @Transactional
    @Override
    public void afterPurchase(String jwt, Long goodsId) {
        // 根据userId 创建锁对象
        RLock redisLock = redissonClient.getLock("order:" + userId);
        // 获取锁对象
        boolean isLock = redisLock.tryLock();
        // 加锁失败，说明当前用户开了多个线程抢商品，但是由于key是SETNX的，所以不能创建key，得等key的TTL到期或释放锁（删除key）
        if (!isLock) {
            return;
        }
        try {
            // 获取代理对象
            proxy.createVoucherOrder(jwt, goodsId);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }


    @Override
    @Transactional
    public void createVoucherOrder(String jwt, Long goodsId) {
        //一人一单要求
        int count = orderClient.findCount(goodsId, jwt);
        if (count > 0) {
            return;
        }
        //扣减库存
        boolean success = stockClient.reduceStock(goodsId, jwt);
        if (!success) {
            return;
        }
        //发送普通消息给MQ
        String topic = "Order";
        String content = jwt + "=" + goodsId + "=" + orderId;
        rocketMQTemplate.convertAndSend(topic, content);
    }

}




