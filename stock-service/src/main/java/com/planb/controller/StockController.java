package com.planb.controller;


import com.planb.common.Result;
import com.planb.service.RepertoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/stock")
@Slf4j
public class StockController {

    @Resource
    private RepertoryService repertoryService;

    /**
     * 扣减库存功能（对外不开放）
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/{id}")
    public boolean reduceStock(@PathVariable("id") Long goodsId) {
        return repertoryService.reduceStock(goodsId);
    }


    /**
     * 回滚库存功能（对外不开放）
     *
     * @param goodsId
     */
    @PostMapping("/{id}")
    public void rollbackStock(@PathVariable("id") Long goodsId) {
        repertoryService.rollbackStock(goodsId);
    }


    @GetMapping("/warmup")
    public Result warmup() {
        return repertoryService.warmup();
    }

}
