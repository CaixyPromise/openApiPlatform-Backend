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
     * */
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    /**
     * 匹配手机号
     * */
    String phoneRegex = "^1[3-9]\\d{9}$";
}
