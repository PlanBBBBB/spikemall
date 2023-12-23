package com.itheima.service;

import com.itheima.common.Result;
import com.itheima.entity.Repertory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 86139
 * @description 针对表【repertory】的数据库操作Service
 * @createDate 2023-04-15 16:26:28
 */
public interface RepertoryService extends IService<Repertory> {

    Result spikeGoods(String jwt, Long goodsId);

    void createVoucherOrder(String jwt, Long goodsId);

    void afterPurchase(String jwt, Long goodsId);

}
