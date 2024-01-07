package com.caixy.project.model.param;

import lombok.Data;

/**
 * 请求载荷实体类
 *
 * @name: com.caixy.project.model.param.PayloadParam
 * @author: CAIXYPROMISE
 * @since: 2024-01-06 15:38
 **/
@Data
public class PayloadParam
{
    private String fieldName;
    private String required;
    private String type;
    private String desc;
}
