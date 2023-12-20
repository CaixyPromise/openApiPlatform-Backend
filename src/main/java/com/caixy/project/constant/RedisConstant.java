package com.caixy.project.constant;

/**
 * @Name: com.caixy.project.constant.RedisConstant
 * @Description: Redis缓存的常量：Key和过期时间
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-20 20:20
 **/
public interface RedisConstant
{
    /**
     * 随机数的redis key
     */
    String NONCE_KEY = "nonce:";
    /**
     * 随机数的过期时间，5分钟
     * */
    Long NONCE_EXPIRE = (60L * 5L);

    // 黑名单的Key
    String BLACKED_LIST_KEY = "BLACKED_LIST_KEY:";
    // 黑名单过期时间
    Long BACKED_LIST_EXPIRE = (60L * 60L * 24L);

    // 错误请求IP的统计redis计数
    String ERROR_COUNT_PREFIX   = "ERROR:COUNT:IP:";
    // 黑名单IP的redis-key
    String BLACKED_IP_PREFIX   = "BLACKED:IP:";
    // 错误IP统计次数过期时间
    Long ERROR_COUNT_DURATION  = (10L * 60L);
    // 黑名单IP的锁定时间
    Long BLACKLIST_DURATION = (60L * 60L);
    // 最大错误次数上限
    int MAX_ERROR_COUNT = 50;

    // 分布式锁基础定义Key
    String REDIS_LOCK_KEY = "REDIS_LOCK:";
    // 分布式锁定义过期时间
    Long REDIS_LOCK_EXPIRE = (10L * 60L);

    // 分布式调用统计锁Key
    String REDIS_INVOKE_LOCK_KEY = REDIS_LOCK_KEY + "REDIS_INVOKE_LOCK:";

}