package com.itheima.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.client.OrderClient;
import com.itheima.client.StockClient;
import com.itheima.common.Result;
import com.itheima.entity.Repertory;
import com.itheima.entity.Users;
import com.itheima.service.RepertoryService;
import com.itheima.mapper.RepertoryMapper;
import com.itheima.utils.RedisIdWorker;
import com.itheima.utils.SimpleRedisLock;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

    @Override
    public Result spikeGoods(String token, Long goodsId) {
        //判断是否满足时间、库存条件
        Repertory spikeGood = getById(goodsId);
        LocalDateTime beginTime = spikeGood.getBeginTime();
        LocalDateTime endTime = spikeGood.getEndTime();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(beginTime)) {
            return Result.fail("秒杀未开始");
        }
        if (now.isAfter(endTime)) {
            return Result.fail("秒杀已结束");
        }
        //判断商品是否还有库存
        if (spikeGood.getStock() <= 0) {
            return Result.fail("库存不足");
        }

        //获取存在redis中的user
        String tokenKey = "login:user" + token;
        String userJson = stringRedisTemplate.opsForValue().get(tokenKey);
        Users user = JSONUtil.toBean(userJson, Users.class);

        //根据userId 创建锁对象
        Long userId = user.getId();
        SimpleRedisLock redisLock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        // 获取锁对象
        boolean isLock = redisLock.tryLock(120);
        // 加锁失败，说明当前用户开了多个线程抢商品，但是由于key是SETNX的，所以不能创建key，得等key的TTL到期或释放锁（删除key）
        if (!isLock) {
            return Result.fail("不允许抢多次商品");
        }
        try {
            // 获取代理对象
            RepertoryService proxy = (RepertoryService) AopContext.currentProxy();
            return proxy.createVoucherOrder(token,goodsId);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }

    @Override
    @Transactional
    public Result createVoucherOrder(String token,Long goodsId) {
        //一人一单要求
        int count = orderClient.findCount(token, goodsId);
        if (count > 0) {
            return Result.fail("该用户以抢购此商品");
        }

        //扣减库存
        boolean success = stockClient.reduceStock(goodsId);
        if (!success) {
            return Result.fail("该商品已抢购完");
        }

        //生成orderId（使用redisIdWorker）
        long orderId = redisIdWorker.nextId("order");

        //发送普通消息给MQ
        String topic = "Order";
        String message = token + "_" + goodsId + "_" + orderId;
        rocketMQTemplate.convertAndSend(topic, message);

        //返回订单id
        return Result.ok(orderId);
    }
}




