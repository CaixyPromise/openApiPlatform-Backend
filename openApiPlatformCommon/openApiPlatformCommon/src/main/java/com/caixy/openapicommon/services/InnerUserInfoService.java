package com.caixy.openapicommon.services;
import com.caixy.openapicommon.model.entity.RequestUserInfo;

/**
 * @Name: com.caixy.openapicommon.services.InnerUserInfoService
 * @Description: 关于用户信息的远程调用接口
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 18:50
 **/
public interface InnerUserInfoService
{
    /**
     * 校验用户密钥并返回是否被允许
     * @param userInfo 请求校验的用户信息
     * @return Long 用户id
     * @return 是否被允许
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:55
     * @version 1.0
     */
    Long verifyUserKey(RequestUserInfo userInfo);
}
