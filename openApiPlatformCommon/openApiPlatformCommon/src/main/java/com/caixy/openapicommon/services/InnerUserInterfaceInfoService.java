package com.caixy.openapicommon.services;

/**
 * @name: com.caixy.openapicommon.services.InnerUserInterfaceInfoService
 * @Description: 内部用户接口信息远程调用
 * @author : CAIXYPROMISE
 * @since : 2023-12-19 18:43
 **/
public interface InnerUserInterfaceInfoService
{
    /**
     * 检查是否可以调用接口: 检查剩余调用次数
     * @param interfaceId 接口id
     * @param userId 用户id
     * @return 有次数 true / 没次数 false
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:46
     * @version 1.0
     */
    boolean isAllowInvoke(long interfaceId, long userId);
}
