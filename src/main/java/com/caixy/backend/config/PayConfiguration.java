package com.caixy.backend.config;

import com.caixy.backend.config.properties.AliPayConfig;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 支付配置
 *
 * @name: com.caixy.backend.config.PayConfiguration
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 04:15
 **/
@Configuration
@AllArgsConstructor
public class PayConfiguration
{

    @Resource
    private AliPayConfig aliPayAccountConfig;

//    @Bean
//    @ConditionalOnMissingBean
//    public WxPayService wxService() {
//        WxPayConfig payConfig = new WxPayConfig();
//        payConfig.setAppId(StringUtils.trimToNull(this.properties.getAppId()));
//        payConfig.setMchId(StringUtils.trimToNull(this.properties.getMchId()));
//        payConfig.setApiV3Key(StringUtils.trimToNull(this.properties.getApiV3Key()));
//        payConfig.setPrivateKeyPath(StringUtils.trimToNull(this.properties.getPrivateKeyPath()));
//        payConfig.setPrivateCertPath(StringUtils.trimToNull(this.properties.getPrivateCertPath()));
//        payConfig.setNotifyUrl(StringUtils.trimToNull(this.properties.getNotifyUrl()));
//
//        // 可以指定是否使用沙箱环境
//        payConfig.setUseSandboxEnv(false);
//        WxPayService wxPayService = new WxPayServiceImpl();
//        wxPayService.setConfig(payConfig);
//        return wxPayService;
//    }

    @Bean
    public void aliPayApi()
    {
        Boolean isSandBox = aliPayAccountConfig.getIsSandbox();
        System.out.println("isSandBox: " + isSandBox);

        System.out.println("aliPayAccountConfig.getAppId(): " + aliPayAccountConfig.getAppId());
        AliPayApiConfig aliPayApiConfig = AliPayApiConfig.builder()
                .setAppId(aliPayAccountConfig.getAppId())
                .setAliPayPublicKey(aliPayAccountConfig.getAlipayPublicKey())
                .setCharset("UTF-8")
                .setPrivateKey(aliPayAccountConfig.getAppPrivateKey())
                .setServiceUrl(isSandBox
                               ? "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
                               : "https://openapi.alipaydev.com/gateway.do")
                // 非沙箱环境
//                .setServiceUrl("https://openapi.alipay.com/gateway.do")
                .setSignType("RSA2")
                .setCertModel(false)
                .build(); // 普通公钥方式
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
    }
}
