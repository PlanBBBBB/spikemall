package com.itheima.service;

import com.itheima.entity.Repertory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【repertory】的数据库操作Service
* @createDate 2023-04-15 15:27:18
*/
public interface RepertoryService extends IService<Repertory> {

    boolean reduceStock(Long goodsId);

    void rollbackStock(Long goodsId);
}
