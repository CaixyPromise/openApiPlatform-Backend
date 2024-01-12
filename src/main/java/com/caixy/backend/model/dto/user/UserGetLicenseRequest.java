package com.caixy.backend.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
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
    @NotNull(message = "password不能为空")
    public String password;
    @NotNull(message = "nonce不能为空")
    public String nonce;
    @NotNull(message = "timestamp不能为空")
    public String timestamp;
    private static final long serialVersionUID = 1L;
}
