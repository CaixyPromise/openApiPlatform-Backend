package com.caixy.project.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis会话统一配置管理，黑名单、限流等
 *
 * @name: com.caixy.project.config.RedissonProperties
 * @author: CAIXYPROMISE
 * @since: 2024-01-07 21:58
 **/
@Getter
@Configuration
@ConfigurationProperties(prefix = "redis-session")
public class RedissonProperties
{
    private Map<String, RedisInstanceProperties> instances = new HashMap<>();

    public void setInstances(Map<String, RedisInstanceProperties> instances)
    {
        this.instances = instances;
    }

    @Data
    public static class RedisInstanceProperties
    {
        private String host;
        private int port;
        private int database;
        private String password;
    }
}
