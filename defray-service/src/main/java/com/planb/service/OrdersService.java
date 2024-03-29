package com.planb.service;

import com.planb.common.Result;
import com.planb.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【orders】的数据库操作Service
* @createDate 2023-04-15 19:32:18
*/
public interface OrdersService extends IService<Orders> {

    Result pay(String jwt, Long orderId);
}
