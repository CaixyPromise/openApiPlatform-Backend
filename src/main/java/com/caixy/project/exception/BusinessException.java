package com.caixy.project.exception;

import com.caixy.project.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author caixy
 */
@Getter
public class BusinessException extends RuntimeException
{
    private final int code;

    public BusinessException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode)
    {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message)
    {
        super(message);
        this.code = errorCode.getCode();
    }
}