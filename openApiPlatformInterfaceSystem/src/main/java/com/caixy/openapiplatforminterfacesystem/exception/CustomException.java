package com.caixy.openapiplatforminterfacesystem.exception;

import com.caixy.openapiplatforminterfacesystem.common.ErrorCode;
import lombok.Getter;

/**
 * @Name: com.caixy.openapiplatforminterfacesystem.exception.CustomException
 * @Description: 自定义异常类
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:22
 **/
@Getter
public class CustomException extends RuntimeException
{
    private final int code;

    public CustomException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    public CustomException(ErrorCode errorCode)
    {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public CustomException(ErrorCode errorCode, String message)
    {
        super(message);
        this.code = errorCode.getCode();
    }
}
