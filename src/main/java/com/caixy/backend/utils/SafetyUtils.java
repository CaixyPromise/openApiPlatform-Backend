package com.caixy.backend.utils;

/**
 * 安全合法性校验工具类
 *
 * @name: com.caixy.backend.utils.SafetyUtils
 * @author: CAIXYPROMISE
 * @since: 2024-01-12 18:25
 **/

public class SafetyUtils
{

    /**
     * 校验时间戳与随机数是否是合法请求
     *
     * @param randomKey            随机数的关联键
     * @param timestamp            时间戳
     * @param random               随机数
     * @param timeout              超时时长
     * @param redisOperatorService redis操作类
     * @return 是否合法
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/12 18:30
     */
    public static boolean checkTimestampAndRandom(String random, String randomKey,
                                                  Long timestamp, Long timeout,
                                                  RedisOperatorService redisOperatorService)
    {
        // 检查时间戳是否合法: 是否超出时间范围内
        if (System.currentTimeMillis() - timestamp > timeout)
        {
            return false;
        }
        else
        {
            // 检查随机数是否合法：是否被使用过
            String redisRandom = redisOperatorService.getString(randomKey + random);
            return redisRandom == null;
        }
    }
}
