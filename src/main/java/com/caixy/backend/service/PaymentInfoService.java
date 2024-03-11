package com.caixy.backend.service;

import com.caixy.backend.model.entity.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.vo.PaymentInfoVo;

/**
* @author CAIXYPROMISE
* @description 针对表【payment_info(付款信息)】的数据库操作Service
* @createDate 2024-03-11 04:07:57
*/
public interface PaymentInfoService extends IService<PaymentInfo> {

    boolean createPaymentInfo(PaymentInfoVo paymentInfoVo);
}
