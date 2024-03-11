package com.caixy.backend.model.dto.product;

import com.caixy.backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 产品查询请求
 *
 * @name: com.caixy.backend.model.dto.product.ProductInfoQueryRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:13
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductInfoQueryRequest extends PageRequest implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 产品名称
     */
    private String name;
    /**
     * 增加积分个数
     */
    private Integer addPoints;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 金额(分)
     */
    private Integer total;

    /**
     * 产品类型（VIP-会员 RECHARGE-充值）
     */
    private String productType;

}