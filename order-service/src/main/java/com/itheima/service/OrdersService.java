package com.itheima.service;

import com.itheima.common.Result;
import com.itheima.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 86139
 * @description 针对表【orders】的数据库操作Service
 * @createDate 2023-04-15 17:25:23
 */
public interface OrdersService extends IService<Orders> {
    int findCount(String token, Long goodsId);

    void saveOrder(String token, Long goodsId, Long orderId);

    Result listByUser(String token);
}
