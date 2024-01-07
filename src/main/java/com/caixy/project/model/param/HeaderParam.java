package com.caixy.project.model.param;

import lombok.Data;

/**
 * 请求头参数实体类
 *
 * @name: com.caixy.project.model.param.HeaderParam
 * @author: CAIXYPROMISE
 * @since: 2024-01-06 15:37
 **/
@Data
public class HeaderParam
{
    private String fieldName;
    private String headerValue;
    private String required;
    private String description;
}