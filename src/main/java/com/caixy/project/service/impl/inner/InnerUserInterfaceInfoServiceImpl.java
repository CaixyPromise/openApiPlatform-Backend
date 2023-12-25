package com.caixy.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caixy.openapicommon.services.InnerUserInterfaceInfoService;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import com.caixy.project.model.entity.UserInterfaceInfo;
import com.caixy.project.service.UserInterfaceInfoService;
import com.caixy.project.utils.service.RedisOperatorService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户接口信息调用接口实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerUserInterfaceInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-20 22:15
 **/
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService
{
    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 检查是否可以调用接口: 检查剩余调用次数
     *
     * @param interfaceId 接口id
     * @param userId      用户id
     * @return 有次数 true / 没次数 false
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/19 18:46
     */
    @Override
    public boolean isAllowInvoke(long interfaceId, long userId)
    {
        // 获取用户调用次数
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceId)
                .eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(queryWrapper);
        return userInterfaceInfo != null && userInterfaceInfo.getLeftNum() > 0;
    }

    /**
     * 更新用户接口调用次数
     * @param interfaceId 接口id
     * @param userId      用户id
     * @param count       调用次数
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/20 22:29
     */
    @Override
    public boolean updateUserInvokeCount(long interfaceId, long userId, int count)
    {
        String lockKey = RedisConstant.REDIS_INVOKE_LOCK_KEY + ":" + interfaceId + ":" + userId;
        String requestId = String.valueOf(userId); // 生成或获取请求标识符

        try {
            // 1. 分布式加锁
            boolean tryLock = redisOperatorService.tryGetDistributedLock(lockKey, requestId, RedisConstant.REDIS_INVOKE_LOCK_EXPIRE_TIME);
            if (!tryLock) {
                // 无法获取锁，调用失败，返回false
                return false;
            }
            // 2. 更新接口调用次数
            userInterfaceInfoService.updateUserInvokeCount(interfaceId, userId, count);
            return true;
        }
        finally
        {
            // 3. 分布式解锁
            redisOperatorService.releaseDistributedLock(lockKey, requestId);
        }
    }
}
