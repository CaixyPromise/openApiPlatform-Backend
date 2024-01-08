package com.caixy.backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取修改凭证
 *
 * @name: com.caixy.project.model.dto.user.UserGetLicenseRequest
 * @author: CAIXYPROMISE
 * @since: 2024-01-03 19:27
 **/
@Data
public class UserGetLicenseRequest implements Serializable
{
    public String password;
    private static final long serialVersionUID = 1L;
}
