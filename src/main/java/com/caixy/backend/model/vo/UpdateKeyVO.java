package com.caixy.backend.model.vo;

import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

/**
 * 更新Key的返回实体类
 *
 * @name: com.caixy.backend.model.vo.UpdateKeyVO
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 14:54
 **/
@Data
public class UpdateKeyVO implements Serializable
{
    private String accessKey;
    private String secretKey;

    private static final long serialVersionUID = 1L;
}
