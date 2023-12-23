package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.Result;
import com.itheima.entity.Repertory;
import com.itheima.service.RepertoryService;
import com.itheima.mapper.RepertoryMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 86139
 * @description 针对表【repertory】的数据库操作Service实现
 * @createDate 2023-04-15 15:27:18
 */
@Service
public class RepertoryServiceImpl extends ServiceImpl<RepertoryMapper, Repertory> implements RepertoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public boolean reduceStock(Long goodsId) {
        LambdaUpdateWrapper<Repertory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Repertory::getGoodsId, goodsId)
                .gt(Repertory::getStock, 0)
                .setSql("stock = stock - 1");
        return update(updateWrapper);
    }

    @Override
    @Transactional
    public void rollbackStock(Long goodsId) {
        LambdaUpdateWrapper<Repertory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Repertory::getGoodsId, goodsId)
                .setSql("stock = stock + 1");
        update(updateWrapper);
    }


    @Transactional
    @Override
    public Result warmup() {
        List<Repertory> repertoryList = list();
        repertoryList.forEach(repertory -> {
            String key = "seckill:stock:" + repertory.getGoodsId();
            String value = String.valueOf(repertory.getStock());
            stringRedisTemplate.opsForValue().set(key, value);
        });
        return Result.ok();
    }
}




