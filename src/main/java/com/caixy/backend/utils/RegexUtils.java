package com.caixy.backend.utils;

import com.caixy.backend.constant.RegexConstants;

import java.util.regex.Pattern;

/**
 * 正则匹配工具类
 *
 * @name: com.caixy.backend.utils.RegexUtils
 * @author: CAIXYPROMISE
 * @since: 2024-01-11 15:58
 **/
public class RegexUtils
{
    /**
     * 匹配是否为邮箱
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/11 16:01
     */
    public static boolean isEmail(String email)
    {
        return isMatch(RegexConstants.emailRegex, email);
    }

    /**
     * 匹配是否为手机号
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/11 1:01
     */
    public static boolean isPhone(String phone)
    {
        return isMatch(RegexConstants.phoneRegex, phone);
    }

    // region 匹配内置方法函数
    // 是否匹配
    private static boolean isMatch(String regex, String str)
    {
        return Pattern.matches(regex, str);
    }
    // endregion
}
