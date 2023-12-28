package com.planb.controller;

import com.planb.common.Result;
import com.planb.mapper.GoodsMapper;
import com.planb.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/good")
@Slf4j
public class GoodController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsMapper goodsMapper;

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
    @GetMapping("/get/{id}")
    public Long getPrice(@PathVariable("id") Long goodsId) {
        return goodsService.getPrice(goodsId);
    }

    /**
     * 通过商品id查询商品(对外不开放)
     *
     * @param goodsId
     * @return
     */
    @PostMapping("/get/{id}")
    public Result getGoods(@PathVariable("id") Long goodsId) {
        return goodsService.getGoods(goodsId);
    }

}
