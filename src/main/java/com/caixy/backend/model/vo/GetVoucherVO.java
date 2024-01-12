package com.caixy.backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取Key请求
 *
 * @name: com.caixy.backend.model.vo.GetVoucherVO
 * @author: CAIXYPROMISE
 * @since: 2024-01-11 21:06
 **/
@Data
public class GetVoucherVO implements Serializable
{
    private String accessKey;
    private String secretKey;
    private static final long serialVersionUID = 1L;
}
