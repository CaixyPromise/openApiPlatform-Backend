package com.caixy.backend.model.vo;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 支付信息封装类
 *
 * @name: com.caixy.backend.model.vo.PaymentInfoVo
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 04:22
 **/
@Data
@NoArgsConstructor
public class PaymentInfoVo {
    private static final long serialVersionUID = 1L;

    private String appid;

    private String mchid;

    private String outTradeNo;

    private String transactionId;

    /**
     * 贸易类型
     */
    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private String bankType;

    private String attach;

    private String successTime;

    private WxPayOrderQueryV3Result.Payer payer;
    @SerializedName(value = "amount")
    private WxPayOrderQueryV3Result.Amount amount;
    @SerializedName(value = "scene_info")
    private WxPayOrderQueryV3Result.SceneInfo sceneInfo;
    @SerializedName(value = "promotion_detail")
    private List<WxPayOrderQueryV3Result.PromotionDetail> promotionDetails;
}
