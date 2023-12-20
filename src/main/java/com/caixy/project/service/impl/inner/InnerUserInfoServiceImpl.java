package com.caixy.project.service.impl.inner;

import com.caixy.openApiPlatformEncryptionAlgorithm.SignUtils;
import com.caixy.openapicommon.model.entity.RequestUserInfo;
import com.caixy.openapicommon.services.InnerUserInfoService;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.mapper.UserMapper;
import com.caixy.project.model.entity.User;
import com.caixy.project.utils.service.RedisService;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * 远程调用查询用户信息接口实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerUserInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-19 22:08
 **/
public class InnerUserInfoServiceImpl implements InnerUserInfoService
{
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    /**
     * 校验用户密钥并返回是否合法
     * @param userInfo 请求的用户信息
     * @return 是否被允许
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:55
     * @version 1.0
     */
    @Override
    public boolean verifyUserKey(RequestUserInfo userInfo)
    {
        // 1. 获取信息
        Long userId = userInfo.getId();
        String accessKey = userInfo.getAccessKey();
        String secretKey = userInfo.getSecretKey();
        // 1.2 获取用户加密的字段
        Long timestamp = Long.parseLong(userInfo.getTimestamp());
        String nonce = userInfo.getNonce();

        // 2. 根据accessKey获取用户信息
        User queryUser = userMapper.selectById(userId);
        if (queryUser == null)
        {
            return false;
        }
        // 2.1 校验timestamp
        // 2.1.1 校验时间戳是否过期
        Long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - timestamp > 5 * 60 || currentTime - timestamp < 0)
        {
            return false;
        }
        // 2.2 校验nonce
        // 2.2.1 校验nonce是否被使用过
        if (redisService.hasKey(RedisConstant.NONCE_KEY + nonce))
        {
            return false;
        }
        // 2.2.2 没有被使用过就把它插入到redis内
        redisService.setString(RedisConstant.NONCE_KEY + nonce, nonce, RedisConstant.NONCE_EXPIRE);

        // 3. 校验用户信息
        // 3.1 校验accessKey
        if (!queryUser.getAccessKey().equals(accessKey))
        {
            return false;
        }
        // 3.2 校验secretKey
        return !SignUtils.validateSecretKey(queryUser.getSecretKey(),
                nonce, timestamp,
                secretKey);
    }

}
