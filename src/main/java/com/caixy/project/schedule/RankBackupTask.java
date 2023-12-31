package com.caixy.project.schedule;

import com.caixy.project.constant.RedisConstant;
import com.caixy.project.mapper.InterfaceInfoMapper;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import com.caixy.project.model.entity.UserInterfaceInfo;
import com.caixy.project.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排行榜信息备份任务
 *
 * @name: com.caixy.project.schedule.RankBackupTask
 * @author: CAIXYPROMISE
 * @since: 2023-12-30 22:42
 **/
//@Component
//@Slf4j
public class RankBackupTask
{
//    @Resource
//    private RedisOperatorService redisOperatorService;
//    @Resource
//    private UserInterfaceInfoMapper userInterfaceInfoMapper;
//    @Resource
//    private InterfaceInfoMapper interfaceInfoMapper;

//    /**
//     * 定时检查和更新排行榜信息。
//     * 这个任务会定期从数据库中查询最新的接口调用信息，并更新Redis中的排行榜。
//     */
//    @Scheduled(cron = "0 */1 * * * ?", zone = "Asia/Shanghai")  // 每隔1分钟执行
//    public void eachMinuteTask()
//    {
//        // backupRankData();
//    }

//    private void backupRankData()
//    {
//        // 从数据库获取最新排行榜数据
//        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(
//                Math.toIntExact(RedisConstant.REDIS_INVOKE_RANK_MAX_SIZE));
//
//        // 获取Redis中当前的排行榜数据
//        Set<String> redisRankKeys = redisOperatorService.zReverseRangeWithScore(RedisConstant.REDIS_INVOKE_RANK_KEY)
//                .stream()
//                .map(ZSetOperations.TypedTuple::getValue)
//                .collect(Collectors.toSet());
//
//        // 检查并更新Redis中的排行榜数据
//        userInterfaceInfoList.forEach(userInterfaceInfo -> {
//            String infoKey = RedisConstant.REDIS_INVOKE_INFO_KEY + userInterfaceInfo.getId();
//            if (!redisRankKeys.contains(infoKey))
//            {
//                // 数据在Redis排行榜中不存在，需要添加
//                addOrUpdateRedisRankData(userInterfaceInfo, infoKey);
//            }
//            else
//            {
//                // 数据存在，检查是否需要更新
//                Map<Object, Object> existingData = redisOperatorService.getHash(infoKey);
//                if (existingData == null || !existingData.equals(userInterfaceInfo))
//                {
//                    // 数据不一致，进行更新
//                    addOrUpdateRedisRankData(userInterfaceInfo, infoKey);
//                }
//                redisRankKeys.remove(infoKey); // 从集合中移除，剩下的将是需要淘汰的数据
//            }
//        });
//
//        // 淘汰不再属于前N名的数据
//        redisRankKeys.forEach(redisOperatorService::delete);
//    }
//
//    private void addOrUpdateRedisRankData(UserInterfaceInfo userInterfaceInfo, String infoKey)
//    {
//        // 根据UserInterfaceInfo构造需要存储的数据
//        HashMap<String, Object> interfaceMap = new HashMap<>();
//        interfaceMap.put("id", userInterfaceInfo.getInterfaceInfoId());
//        interfaceMap.put("totalNum", userInterfaceInfo.getTotalNum());
//        redisOperatorService.setHashMap(infoKey, interfaceMap, null);
//        redisOperatorService.zAdd(RedisConstant.REDIS_INVOKE_RANK_KEY, infoKey, userInterfaceInfo.getTotalNum());
//    }
}
