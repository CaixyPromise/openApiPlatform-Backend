package com.caixy.openapicommon.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 *
 * @TableName user
 */
@Data
public class RequestUserInfo implements Serializable
{
    /**
     * id
     */
    private Long id;

    // 会话id
    private String sessionId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色：user / admin
     */
    private String userRole;

    /**
     * accessKey
     */
    private String accessKey;

    /**
    * 请求时间戳
    * */
    private String timestamp;

    /**
     * 请求随机数
     * */
    private String nonce;

    /**
     * secretKey
     */
    private String secretKey;

    private static final long serialVersionUID = 1L;
}