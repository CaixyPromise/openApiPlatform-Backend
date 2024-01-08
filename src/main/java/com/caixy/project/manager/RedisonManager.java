package com.caixy.project.manager;

import com.caixy.project.common.ErrorCode;
import com.caixy.project.config.RedissonProperties;
import com.caixy.project.exception.ThrowUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RedisSession会话管理，用来配置分布式redis配置，根据要求客户端类型加载对应的客户端
 * @name: com.caixy.project.manager.RedisonManager
 * @author: CAIXYPROMISE
 * @since: 2024-01-07 22:01
 **/
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisonManager
{
    @Resource
    private RedissonProperties redissonProperties;

    public RedissonClient getRedissonClient(String clientName)
    {
        RedissonProperties.RedisInstanceProperties properties = redissonProperties.getInstances().get(clientName);
        ThrowUtils.throwIf(properties == null, ErrorCode.SYSTEM_ERROR, "Redisson配置不存在");
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword() == null ? null : properties.getPassword());
        return Redisson.create(config);
    }
}
