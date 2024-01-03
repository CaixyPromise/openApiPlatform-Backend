package com.caixy.project.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户签名视图
 *
 * @name: com.caixy.project.model.vo.SignatureVO
 * @author: CAIXYPROMISE
 * @since: 2024-01-03 20:02
 **/
@Data
public class SignatureVO implements Serializable
{
    private String signature;
    private static final long serialVersionUID = 1L;
}
