package com.caixy.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品状态枚举
 *
 * @name: com.caixy.backend.model.enums.ProductInfoStatusEnum
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:20
 **/
public enum ProductInfoStatusEnum {

    /**
     * 发布
     */
    ONLINE("开启", 1),
    /**
     * 下线
     */
    OFFLINE("关闭", 2),

    /**
     * 审计
     */
    AUDITING("审核中", 0);

    private final String text;

    private final int value;

    ProductInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值
     *
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
