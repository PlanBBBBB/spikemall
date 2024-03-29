package com.planb.mapper;

import com.planb.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【orders】的数据库操作Mapper
* @createDate 2023-04-15 19:32:18
* @Entity com.itheima.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




