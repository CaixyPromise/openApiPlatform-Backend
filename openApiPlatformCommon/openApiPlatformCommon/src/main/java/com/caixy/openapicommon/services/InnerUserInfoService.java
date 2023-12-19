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
     * 检查是否可以调用接口: 检查是否在黑名单中
     * @author CAIXYPROMISE
     * @param accessKey 用户accessKey
     * @return 是否在黑名单中 是 true, 否 false
     * @since  2023/12/19 18:47
     * @version 1.0
     */
    boolean isInsideBlackList(String accessKey);

    /**
     * 校验用户密钥并返回是否被允许
     * @param userInfo 请求校验的用户信息
     * @return 是否被允许
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:55
     * @version 1.0
     */
    boolean verifyUserKey(RequestUserInfo userInfo);
}
