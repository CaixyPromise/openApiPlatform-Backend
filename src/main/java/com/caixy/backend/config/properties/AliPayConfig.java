package com.caixy.backend.config.properties;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 支付宝支付配置
 *
 * @name: com.caixy.backend.config.properties.AliPayConfig
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 03:47
 **/
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    /**
     * 同步返回的url
     */
    private String returnUrl;

    /**
     * 是否使用沙箱
     */
    private Boolean isSandbox;

    /**
     * 卖家id
     */
    private String sellerId;
}
