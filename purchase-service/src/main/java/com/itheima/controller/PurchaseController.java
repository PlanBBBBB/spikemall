package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.service.RepertoryService;
import com.itheima.utils.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/purchase")
@Slf4j
public class PurchaseController {

    @Resource
    private RepertoryService repertoryService;

    /**
     * 秒杀商品功能，请求头必须携带token
     *
     * @param goodsId
     * @return
     */
    @PostMapping("/spike/{id}")
    public Result spikeGoods(HttpServletRequest request, @PathVariable("id") Long goodsId) {
        Long userId = UserToken.getToken(request);
        return repertoryService.spikeGoods(userId, goodsId);
    }

}
