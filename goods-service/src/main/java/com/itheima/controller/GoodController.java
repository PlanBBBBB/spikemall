package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.entity.Goods;
import com.itheima.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/get/{id}")
    public Long getPrice(@PathVariable("id") Long goodsId) {
        return goodsService.getById(goodsId).getPrice();
    }


    /**
     * 添加商品（仅管理员可用）
     *
     * @param good
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody Goods good) {
        return goodsService.saveGood(good);
    }


    /**
     * 删除商品（仅管理员可用）
     *
     * @param goodsId
     * @return
     */
    @PostMapping("/delete/{goodsId}")
    public Result delete(@PathVariable Long goodsId) {
        goodsService.removeById(goodsId);
        return Result.ok();
    }


    /**
     * 修改商品（仅管理员可用）
     *
     * @param good
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods good) {
        goodsService.updateById(good);
        return Result.ok();
    }

}
