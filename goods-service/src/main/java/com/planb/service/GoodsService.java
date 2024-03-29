package com.planb.service;

import com.planb.common.Result;
import com.planb.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 86139
 * @description 针对表【goods】的数据库操作Service
 * @createDate 2023-04-15 16:11:23
 */
public interface GoodsService extends IService<Goods> {
    Result listByRedis();

    Long getPrice(Long goodsId);

    Result getGoods(Long goodsId);
}
