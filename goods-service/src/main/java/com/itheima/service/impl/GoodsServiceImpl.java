package com.itheima.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.Result;
import com.itheima.entity.Goods;
import com.itheima.service.GoodsService;
import com.itheima.mapper.GoodsMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author 86139
 * @description 针对表【goods】的数据库操作Service实现
 * @createDate 2023-04-15 16:11:23
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Result listByRedis() {
        //先从缓存中查
        String goodsKey = "cache:goods";
        String goodsKeyJson = stringRedisTemplate.opsForValue().get(goodsKey);
        //查到了就返回数据
        if (goodsKeyJson != null) {
            return Result.ok(JSONUtil.toList(goodsKeyJson, Goods.class));
        }
        //查不到再从数据库查
        List<Goods> goodsList = query().orderByAsc("id").list();
        if (goodsList == null) {
            stringRedisTemplate.opsForValue().setIfAbsent(goodsKey, "", 10, TimeUnit.SECONDS);
            return Result.fail("商铺类型发生错误");
        }
        //将从数据库查询结果存入缓存
        Random random = new Random();
        int expirationTime = random.nextInt(31) + 30;
        stringRedisTemplate.opsForValue().setIfAbsent(goodsKey, JSONUtil.toJsonStr(goodsList), expirationTime, TimeUnit.MINUTES);
        return Result.ok(goodsList);
    }

    @Override
    public Long getPrice(Long goodsId) {
        Result result = getGoods(goodsId);
        if (result.getSuccess()) {
            Goods goods = (Goods) result.getData();
            return goods.getPrice();
        }
        return -1L;
    }

    @Override
    public Result getGoods(Long goodsId) {
        //先从缓存中查
        String goodsKey = "cache:goods" + goodsId;
        String goodsKeyJson = stringRedisTemplate.opsForValue().get(goodsKey);
        //查到了就返回数据
        if (goodsKeyJson != null) {
            return Result.ok(JSONUtil.toList(goodsKeyJson, Goods.class));
        }
        //查不到再从数据库查
        Goods goods = this.getById(goodsId);
        if (goods == null) {
            stringRedisTemplate.opsForValue().setIfAbsent(goodsKey, "", 10, TimeUnit.SECONDS);
            return Result.fail("查询商品发生错误");
        }
        //将从数据库查询结果存入缓存
        Random random = new Random();
        int expirationTime = random.nextInt(31) + 30;
        stringRedisTemplate.opsForValue().setIfAbsent(goodsKey, JSONUtil.toJsonStr(goods), expirationTime, TimeUnit.MINUTES);
        return Result.ok(goods);
    }
}




