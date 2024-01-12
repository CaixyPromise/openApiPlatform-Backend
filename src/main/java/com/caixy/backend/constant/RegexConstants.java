package com.caixy.backend.constant;

/**
 * 正则匹配表达式常量
 *
 * @name: com.caixy.backend.constant.RegexConstants
 * @author: CAIXYPROMISE
 * @since: 2024-01-11 15:33
 **/
public interface RegexConstants
{
    /**
     * 匹配邮箱
     */
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    /**
     * 匹配手机号
     */
    String phoneRegex = "^1[3-9]\\d{9}$";

    /**
     * 匹配密码: 8-20 位，字母、数字、英文特殊字符;
     */
    String passwordRegex = "^[A-Za-z0-9!@#$%^&*\\\\\\\\(\\\\\\\\)?><\\\":/.,';{}]{8,20}$";

    /**
     * 匹配网址
     * */
    String urlRegex = "^(http[s]?://)?([\\w.-]+(?:\\.[\\w.-]+)+|localhost)?[\\w\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*$";

    /**
     * 匹配账号: 4-16 位，字母、数字
     */
    String accountRegex = "^[a-zA-Z][a-zA-Z0-9_-]{3,16}$";
}
