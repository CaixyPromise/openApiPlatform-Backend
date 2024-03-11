package com.caixy.backend.model.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单信息返回封装类
 *
 * @name: com.caixy.backend.model.vo.OrderVo
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 03:12
 **/
@Data
public class OrderVo implements Serializable
{
    private static final long serialVersionUID = -7340958009391771093L;
    private List<ProductOrderVo> records;
    private long total;
    private long size;
    private long current;
    private List<OrderItem> orders;
    private boolean optimizeCountSql;
    private boolean searchCount;
    private boolean optimizeJoinOfCountSql;
    private String countId;
    private Long maxLimit;
}
