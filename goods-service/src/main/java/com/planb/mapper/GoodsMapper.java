package com.planb.mapper;

import com.planb.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【goods】的数据库操作Mapper
* @createDate 2023-04-15 16:11:23
* @Entity com.itheima.entity.Goods
*/
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

}




