package com.caixy.project.service.impl.inner;

import com.caixy.openapicommon.services.InnerBackedListService;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.utils.service.RedisOperatorService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部黑名单操作方法实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerBackedListServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-20 21:35
 **/
@DubboService
public class InnerBackedListServiceImpl implements InnerBackedListService
{
    @Resource
    private RedisOperatorService redisOperatorService;

    @Override
    public void recordError(String ip)
    {
        String errorKey = RedisConstant.ERROR_COUNT_PREFIX + ip;
        if (!redisOperatorService.hasKey(errorKey))
        {
            redisOperatorService.setString(errorKey, "1", RedisConstant.ERROR_COUNT_DURATION);
        }
        else
        {
            int errors = Integer.parseInt(redisOperatorService.getString(errorKey));
            if (errors >= RedisConstant.MAX_ERROR_COUNT)
            {
                addToBlackList(ip);
            }
            else
            {
                redisOperatorService.setString(errorKey, String.valueOf(errors + 1), RedisConstant.ERROR_COUNT_DURATION);
            }
        }
    }

    @Override
    public void addToBlackList(String ip)
    {
        redisOperatorService.setString(RedisConstant.BACKED_LIST_EXPIRE + ip, "1", RedisConstant.BACKED_LIST_EXPIRE);
    }

    @Override
    public boolean isInsideBlackList(String ip)
    {
        boolean isExist = redisOperatorService.hasKey(RedisConstant.BLACKED_IP_PREFIX + ip);
        if (isExist)
        {
            redisOperatorService.refreshExpire(RedisConstant.BLACKED_IP_PREFIX + ip, RedisConstant.BACKED_LIST_EXPIRE);
            return true;
        }
        return false;
    }
}
