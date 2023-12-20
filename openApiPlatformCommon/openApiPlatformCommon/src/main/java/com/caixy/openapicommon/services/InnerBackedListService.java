package com.caixy.openapicommon.services;

/**
 * @Name: com.caixy.openapicommon.services.InnerBackedListService
 * @Description: 内部黑名单远程调用接口
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-20 21:04
 **/
public interface InnerBackedListService
{
    /**
     * 记录错误的ip和次数
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/0 21:04
     */
    void recordError(String ip);

    /**
     * 添加ip进入黑名单
     * @author CAIXYPROMISE
     * @since 2023/12/20 21:05
     * @version 1.0
     */
    void addToBlackList(String ip);

    /**
     * 检查是否可以调用接口: 检查是否在黑名单中
     * @author CAIXYPROMISE
     * @param accessKey 用户accessKey
     * @return 是否在黑名单中 是 true, 否 false
     * @since  2023/12/19 18:47
     * @version 1.0
     */
    boolean isInsideBlackList(String accessKey);
}
