package com.caixy.openapiplatforminterfacesystem.exception;

import com.caixy.openapiplatforminterfacesystem.common.BaseResponse;
import com.caixy.openapiplatforminterfacesystem.common.ErrorCode;
import com.caixy.openapiplatforminterfacesystem.common.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Name: com.caixy.openapiplatforminterfacesystem.exception.GlobalExceptionHandler
 * @Description: 全局异常处理器设置
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:04
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    /**
     * 自定义异常处理类
     * @return 错误响应体
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:28
     * @updatedDate 2023/12/19 16:28
     * @version 1.0
     */
    @ExceptionHandler(CustomException.class)
    public BaseResponse<?> customExceptionHandler(CustomException e)
    {
        log.error("CustomException:" + e.getMessage(), e);
        return ResponseUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 默认异常处理类
     * @return 错误响应体
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:29
     * @updatedDate 2023/12/19 16:29
     * @version 1.0
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e)
    {
        log.error("RuntimeException:" + e.getMessage(), e);
        return ResponseUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
