package com.caixy.backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 产品信息
 * @TableName product_info
 */
@TableName(value ="product_info")
@Data
public class ProductInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 金额(分)
     */
    private Long total;

    /**
     * 增加积分个数
     */
    private Long addPoints;

    /**
     * 产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）
     */
    private String productType;

    /**
     * 商品状态（0- 默认下线 1- 上线）
     */
    private Integer status;

    /**
     * 过期时间
     */
    private Date expirationTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}