package com.planb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @TableName orders
 */
@TableName(value ="orders")
@Data
public class Orders implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 下单用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long goodId;

    /**
     * 订单状态（0未支付，1已支付）
     */
    private Integer status;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 结账时间
     */
    private LocalDateTime checkTime;

    /**
     * 实收金额
     */
    private Long amount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}