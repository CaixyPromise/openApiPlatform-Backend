package com.caixy.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caixy.openApiPlatformEncryptionAlgorithm.SignUtils;
import com.caixy.openapicommon.model.entity.RequestUserInfo;
import com.caixy.openapicommon.services.InnerUserInfoService;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.mapper.UserMapper;
import com.caixy.project.model.entity.User;
import com.caixy.project.utils.service.RedisOperatorService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 远程调用查询用户信息接口实现
 *
 * @name: com.caixy.project.service.impl.inner.InnerUserInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-19 22:08
 **/
@DubboService
public class InnerUserInfoServiceImpl implements InnerUserInfoService
{
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisOperatorService redisOperatorService;

    /**
     * 校验用户密钥并返回是否合法
     * @param userInfo 请求的用户信息
     * @return 是否被允许
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:55
     * @version 1.0
     */
    @Override
    public Long verifyUserKey(RequestUserInfo userInfo)
    {
        System.out.println("Received UserInfo: "+ userInfo);
        // 1. 获取信息
        String accessKey = userInfo.getAccessKey();
        String secretKey = userInfo.getSecretKey();
        // 1.2 获取用户加密的字段
        Long timestamp = Long.parseLong(userInfo.getTimestamp());
        String nonce = userInfo.getNonce();

        // 2. 根据accessKey获取用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        User queryUser = userMapper.selectOne(queryWrapper);
        System.out.println("Search AccessKey..." + accessKey);
        if (queryUser == null)
        {
            return -1L;
        }
        System.out.println("queryUser = " + queryUser);
        // 2.1 校验timestamp
        // 2.1.1 校验时间戳是否过期
        Long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - timestamp > 5 * 60 || currentTime - timestamp < 0)
        {
            return -1L;
        }
        System.out.println("currentTime = " + currentTime);
        // 2.2 校验nonce
        // 2.2.1 校验nonce是否被使用过
        if (redisOperatorService.hasKey(RedisConstant.NONCE_KEY + nonce))
        {
            return -1L;
        }
        System.out.println("nonce = " + nonce);
        // 2.2.2 没有被使用过就把它插入到redis内
        redisOperatorService.setString(RedisConstant.NONCE_KEY + nonce, nonce, RedisConstant.NONCE_EXPIRE);

        // 3. 校验用户信息
        // 3.1 校验accessKey
        if (!queryUser.getAccessKey().equals(accessKey))
        {
            return -1L;
        }
        System.out.println("accessKey = " + queryUser.getAccessKey());
        System.out.println("用户的SecretKey = " + queryUser.getSecretKey());
        System.out.println("加密后:" + SignUtils.encodeSecretKey(queryUser.getSecretKey(), nonce, timestamp));
        System.out.println("传入的SecretKey =" + secretKey);
        // 3.2 校验secretKey
        if (!SignUtils.validateSecretKey(queryUser.getSecretKey(),
                nonce, timestamp,
                secretKey))
        {
            return -1L;
        }
        System.out.println("PASS secretKey");
        // 4. 返回用户Id
        return queryUser.getId();
    }

}
