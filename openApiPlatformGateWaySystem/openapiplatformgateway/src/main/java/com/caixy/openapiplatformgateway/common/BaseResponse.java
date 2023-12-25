package com.caixy.openapiplatformgateway.common;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * @Name: com.caixy.openapiplatformgateway.common.BaseResponse
 * @Description: 全局响应基类类
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:05
 **/
@AllArgsConstructor
public class BaseResponse<T> implements Serializable
{
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data)
    {
        this(code, data,  "");
    }

    public BaseResponse(ErrorCode errorCode)
    {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
