package com.caixy.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品类型状态枚举
 *
 * @name: com.caixy.backend.model.enums.ProductTypeStatusEnum
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:21
 **/
public enum ProductTypeStatusEnum {

    /**
     * VIP会员
     */
    VIP("VIP会员", "VIP"),
    /**
     * 余额充值
     */
    RECHARGE("余额充值", "RECHARGE"),

    /**
     * 充值活动
     */
    RECHARGE_ACTIVITY("充值活动", "RECHARGEACTIVITY");
    private final String text;

    private final String value;

    ProductTypeStatusEnum(String text, String value) {
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
