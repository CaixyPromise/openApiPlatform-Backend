package com.caixy.project.service.impl.inner;

import com.caixy.openapicommon.model.entity.RequestUserInfo;
import com.caixy.openapicommon.services.InnerUserInfoService;

/**
 * 远程调用查询用户信息接口实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerUserInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-19 22:08
 **/
public class InnerUserInfoServiceImpl implements InnerUserInfoService
{
    /**
     * 检查是否可以调用接口: 检查是否在黑名单中
     * @author CAIXYPROMISE
     * @param accessKey 用户accessKey
     * @return 是否在黑名单中 是 true, 否 false
     * @since  2023/12/19 18:47
     * @version 1.0
     */
    @Override
    public boolean isInsideBlackList(String accessKey)
    {
        return false;
    }


    /**
     * 校验用户密钥并返回是否被允许
     * @param secretKey 用户secretKey
     * @param accessKey 用户accessKey
     * @return 是否被允许
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:55
     * @version 1.0
     */
    @Override
    public boolean verifyUserKey(RequestUserInfo userInfo)
    {
        return false;
    }
}
