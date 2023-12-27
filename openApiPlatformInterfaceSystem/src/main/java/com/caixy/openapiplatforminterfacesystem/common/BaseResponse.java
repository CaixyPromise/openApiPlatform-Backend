package com.caixy.openapiplatforminterfacesystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Name: com.caixy.openapiplatforminterfacesystem.common.BaseResponse
 * @Description: 全局响应基类类
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:05
 **/
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

