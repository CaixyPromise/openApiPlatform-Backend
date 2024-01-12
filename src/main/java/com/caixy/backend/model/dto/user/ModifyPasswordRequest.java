package com.caixy.backend.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改密码请求
 *
 * @name: com.caixy.backend.model.dto.user.ModifyPasswordRequest
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:15
 **/
@Data
public class ModifyPasswordRequest implements Serializable
{
    /**
     * 签名信息
     */
    @NotNull(message = "签名信息不能为空")
    private String signature;
    /**
     * 原密码
     */
    @NotNull(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotNull(message = "新密码不能为空")
    private String newPassword;
    /**
     * 确认密码
     */
    @NotNull(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 邮箱验证
     */
    @NotNull(message = "邮箱验证不能为空")
    private String emailCode;

    private static final long serialVersionUID = 1L;
}
