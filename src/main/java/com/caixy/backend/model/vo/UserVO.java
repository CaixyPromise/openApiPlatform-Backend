package com.caixy.backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户视图
 *
 * @TableName user
 */
@Data
public class UserVO implements Serializable
{
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}