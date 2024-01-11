package com.caixy.backend.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改邮箱请求体类
 *
 * @name: com.caixy.backend.model.dto.user.ModifyEmailCaptchaRequest
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:34
 **/
@Data
public class ModifyUserEmailRequest implements Serializable
{
    /**
     * 认证信息
     */
    @NotNull(message = "认证信息不能为空")
    private String signature;

    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空")
    private String code;
    /**
     * 邮箱信息
     */
    @NotNull(message = "邮箱不能为空")
    private String email;
    private final static long serialVersionUID = 1L;
}
