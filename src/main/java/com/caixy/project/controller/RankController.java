package com.caixy.project.controller;

import com.caixy.project.annotation.AuthCheck;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.service.RankService;
import com.caixy.project.utils.JsonUtils;
import com.caixy.project.utils.RedisOperatorService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

/**
 * 排行榜接口控制器
 *
 * @name: com.caixy.project.controller.RankController
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 22:00
 **/
@RestController
@RequestMapping("/rank")
public class RankController
{
    @Resource
    private RankService rankService;

    @Resource
    private RedisOperatorService redisOperatorService;

    /**
     * 获取调用数据的排行榜信息
     * 需要管理员权限
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/30 22:40
     */
    @PostMapping("/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<?> getInvokeRank()
    {
        // 设置请求body
        HashMap<Double, Object> body = new HashMap<>();
        // 1. 从 排行榜获取 排行榜信息
        Set<ZSetOperations.TypedTuple<String>> rankResult =  rankService.rankWithScore(RedisConstant.REDIS_INVOKE_RANK_KEY);
        // 2. 从 Redis 获取 排行榜的key对应的接口信息
        rankResult.forEach(tuple -> {
            body.put(tuple.getScore(),
                redisOperatorService.getHash(RedisConstant.REDIS_INVOKE_INFO_KEY + tuple.getValue()));
        });
        // 3. 返回接口信息
        return ResultUtils.success(body);
    }
}
