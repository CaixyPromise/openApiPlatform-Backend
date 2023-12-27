package com.caixy.openapiplatforminterfacesystem.common;

/**
 * @Name: com.caixy.openapiplatforminterfacesystem.common.ResponseUtils
 * @Description: 全局响应类
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:15
 **/
public class ResponseUtils
{
    /**
     * 响应成功
     *
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:16
     * @updatedDate 2023/12/19 16:16
     * @version 1.0
     */
    public static<T> BaseResponse<T> success(T data)
    {
        return new BaseResponse<>(0, data, "success");
    }

    /**
     * 响应失败
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:16
     * @updatedDate 2023/12/19 16:16
     * @version 1.0
     */
    public static BaseResponse<?> error(ErrorCode errorCode)
    {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 响应错误: 传入自定义响应码和响应消息
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:18
     * @updatedDate 2023/12/19 16:18
     * @version 1.0
     */
    public static BaseResponse<?> error(int errorCode, String msg)
    {
        return new BaseResponse<>(errorCode, null, msg);
    }

    /**
     * 响应错误: 传入内置错误码和响应消息
     * @author CAIXYPROMISE
     * @createdDate 2023/12/19 16:19
     * @updatedDate 2023/12/19 16:19
     * @version 1.0
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String msg)
    {
        return new BaseResponse<>(errorCode.getCode(), null, msg);
    }

}
