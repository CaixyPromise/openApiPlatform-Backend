package com.caixy.backend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.mapper.PaymentInfoMapper;
import com.caixy.backend.model.entity.PaymentInfo;
import com.caixy.backend.model.vo.PaymentInfoVo;
import com.caixy.backend.service.PaymentInfoService;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author CAIXYPROMISE
 * @description 针对表【payment_info(付款信息)】的数据库操作Service实现
 * @createDate 2024-03-11 04:07:57
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
        implements PaymentInfoService
{
    @Override
    public boolean createPaymentInfo(PaymentInfoVo paymentInfoVo)
    {
        String transactionId = paymentInfoVo.getTransactionId();
        String tradeType = paymentInfoVo.getTradeType();
        String tradeState = paymentInfoVo.getTradeState();
        String tradeStateDesc = paymentInfoVo.getTradeStateDesc();
        String successTime = paymentInfoVo.getSuccessTime();
        WxPayOrderQueryV3Result.Payer payer = paymentInfoVo.getPayer();
        WxPayOrderQueryV3Result.Amount amount = paymentInfoVo.getAmount();

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(paymentInfoVo.getOutTradeNo());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType(tradeType);
        paymentInfo.setTradeState(tradeState);
        if (StringUtils.isNotBlank(successTime))
        {
            paymentInfo.setSuccessTime(successTime);
        }
        paymentInfo.setOpenid(payer.getOpenid());
        paymentInfo.setPayerTotal(Long.valueOf(amount.getPayerTotal()));
        paymentInfo.setCurrency(amount.getCurrency());
        paymentInfo.setPayerCurrency(amount.getPayerCurrency());
        paymentInfo.setTotal(Long.valueOf(amount.getTotal()));
        paymentInfo.setTradeStateDesc(tradeStateDesc);
        paymentInfo.setContent(JSONUtil.toJsonStr(paymentInfoVo));
        return this.save(paymentInfo);
    }
}




