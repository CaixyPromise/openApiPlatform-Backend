package com.caixy.project.utils.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作类
 *
 * @name: com.caixy.project.utils.service.RedisService
 * @author: CAIXYPROMISE
 * @since: 2023-12-20 20:14
 **/
@Service
@AllArgsConstructor
public class RedisService
{
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 删除Key的数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/0 20:19
     */
    public void delete(String key)
    {
        stringRedisTemplate.delete(key);
    }

    /**
     * 刷新过期时间
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/1220 20:18
     */
    public void refreshExpire(String key, long expire)
    {
        stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 获取字符串数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/1220 20:18
     */
    public String getString(String key)
    {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取哈希数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/1220 20:18
     */
    public Map<Object, Object> getHash(String key)
    {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 放入hash类型的数据
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/20 2:16
     */
    public void setHashMap(String key, HashMap<String, Object> data, Long expire)
    {
        stringRedisTemplate.opsForHash().putAll(key, data);
        if (expire != null)
        {
            refreshExpire(key, expire);
        }
    }

    /**
     * 放入字符串类型的字符
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/0 20:16
     */
    public void setString(String key, String value, Long expire)
    {
        if (expire != null)
        {
            stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        }
        else
        {
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 获取是否有对应的Key值存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/20 2:25
     */
    public boolean hasKey(String key)
    {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

}
