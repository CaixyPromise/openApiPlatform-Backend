package com.caixy.backend.model.dto.user;

import lombok.Data;

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
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
    /**
     * 确认密码
     */
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}
