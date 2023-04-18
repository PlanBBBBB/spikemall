package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/good")
@Slf4j
public class GoodController {

    @Resource
    private GoodsService goodsService;

    /**
     * 查看商品列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        return goodsService.listByRedis();
    }

    /**
     * 通过商品id查询价格(对外不开放)
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/{id}")
    public Long getPrice(@PathVariable("id") Long goodsId) {
        return goodsService.getById(goodsId).getPrice();
    }

}
