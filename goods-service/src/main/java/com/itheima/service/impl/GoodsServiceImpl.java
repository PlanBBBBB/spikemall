package com.itheima.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.Result;
import com.itheima.entity.Goods;
import com.itheima.service.GoodsService;
import com.itheima.mapper.GoodsMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
            return Result.fail("商铺类型发生错误");
        }
        //将从数据库查询结果存入缓存
        stringRedisTemplate.opsForValue().setIfAbsent(goodsKey, JSONUtil.toJsonStr(goodsList), 30, TimeUnit.MINUTES);
        return Result.ok(goodsList);
    }

    @Override
    public Result saveGood(Goods good) {
        if (good.getName() == null) {
            return Result.fail("商品名不能为空");
        }
        if (good.getPrice() == null) {
            return Result.fail("商品价格不能为空");
        }
        save(good);
        return Result.ok();
    }
}




