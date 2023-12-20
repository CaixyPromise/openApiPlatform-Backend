package com.caixy.project.service.impl.inner;

import com.caixy.openapicommon.services.InnerUserInterfaceInfoService;
import com.caixy.project.mapper.InterfaceInfoMapper;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import com.caixy.project.utils.service.RedisService;

import javax.annotation.Resource;

/**
 * 内部用户接口信息调用接口实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerUserInterfaceInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-20 22:15
 **/
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService
{
    @Resource
    private RedisService redisService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    /**
     * 检查是否可以调用接口: 检查剩余调用次数
     * @param interfaceId 接口id
     * @param userId 用户id
     * @return 有次数 true / 没次数 false
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:46
     * @version 1.0
     */
    @Override
    public boolean isAllowInvoke(long interfaceId, long userId)
    {
        return false;
    }

    /**
     * 更新接口调用次数
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/20 22:29
     */
    @Override
    public boolean updateInvokeCount(long interfaceId, long userId)
    {
        return false;
    }
}
