package com.caixy.backend.model.dto.PayOrder;

import lombok.Data;

import java.io.Serializable;

/**
 * 付款创建请求
 *
 * @name: com.caixy.backend.model.dto.PayOrder.PayCreateRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 03:10
 **/
@Data
public class PayCreateRequest implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 接口id
     */
    private String productId;

    /**
     * 支付类型
     */
    private String payType;

}