package com.caixy.backend.model.dto.captcha;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改邮箱验证码请求类
 *
 * @name: com.caixy.backend.model.dto.captcha.ModifyEmailCaptchaRequest
 * @author: CAIXYPROMISE
 * @since: 2024-01-11 18:25
 **/
@Data
public class ModifyEmailCaptchaRequest implements Serializable
{
    /**
     * 认证信息
     */
    private String signature;
    /**
     * 操作类型 0: 修改邮箱 1: 验证邮箱
     */
    @NotNull(message = "操作类型不能为空")
    private int eventType;
    /**
     * 新的邮箱
     */
    private String newEmail;

    /**
     * 验证码
     */
    private String code;
    private final static long serialVersionUID = 1L;
}
