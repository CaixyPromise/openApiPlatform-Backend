package com.caixy.backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品订单
 * @TableName product_order
 */
@TableName(value ="product_order")
@Data
public class ProductOrder implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 二维码地址
     */
    private String codeUrl;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String orderName;

    /**
     * 金额(分)
     */
    private Long total;

    /**
     * 交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)
     */
    private String status;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    private String payType;

    /**
     * 商品信息
     */
    private String productInfo;

    /**
     * 支付宝formData
     */
    private String formData;

    /**
     * 增加积分个数
     */
    private Long addPoints;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}