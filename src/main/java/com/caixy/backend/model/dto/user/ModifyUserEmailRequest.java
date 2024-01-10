package com.caixy.backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改邮箱请求体类
 *
 * @name: com.caixy.backend.model.dto.user.ModifyUserEmailRequest
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:34
 **/
@Data
public class ModifyUserEmailRequest implements Serializable
{
    private String userId;
    private String oldEmail;
    private String newEmail;
    private String code;
    private final static long serialVersionUID = 1L;
}
