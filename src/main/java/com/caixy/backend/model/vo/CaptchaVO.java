package com.caixy.backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 验证码返回类
 *
 * @name: com.caixy.project.model.vo.CaptchaVO
 * @author: CAIXYPROMISE
 * @since: 2024-01-02 16:06
 **/
@Data
public class CaptchaVO implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String codeImage;
    private String uuid;
}
