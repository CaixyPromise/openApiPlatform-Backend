package com.caixy.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付类型枚举类型
 *
 * @name: com.caixy.backend.model.enums.PayTypeStatusEnum
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:20
 **/
public enum PayTypeStatusEnum {

    /**
     * 微信支付
     */
    WECHAT("微信支付", "WX"),
    /**
     * 支付宝支付
     */
    ALIPAY("支付宝支付", "ALIPAY");

    private final String text;

    private final String value;

    PayTypeStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 得到值
     * 获取值列表
     *
     * @return {@link List}<{@link Integer}>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
