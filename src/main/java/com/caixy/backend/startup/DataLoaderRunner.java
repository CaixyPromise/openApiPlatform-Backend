package com.caixy.backend.startup;

import com.caixy.backend.constant.RedisConstant;
import com.caixy.backend.mapper.InterfaceInfoMapper;
import com.caixy.backend.mapper.UserInterfaceInfoMapper;
import com.caixy.backend.model.entity.InterfaceInfo;
import com.caixy.backend.model.entity.UserInterfaceInfo;
import com.caixy.backend.utils.RedisOperatorService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务启动数据预热类
 *
 * @name: com.caixy.project.startup.DataLoaderRunner
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 22:39
 **/
@Component
public class DataLoaderRunner implements ApplicationRunner
{
    @Resource
    private RedisOperatorService redisOperatorService;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;


    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        // 接口调用次数排行榜初始化: 弃用
//        invokeRankInitial();
    }

    /**
     * 接口调用次数统计的热点数据统计
     * 1. 统计调用次数最多的接口信息
     * 2. 把接口调用次数最多的接口信息，存储到Redis中
     * 方法: 我们首先查询出调用次数最多的接口信息，然后将每个接口的ID、名称和总调用次数以Hash形式存储到Redis中。
     *      同时，我们还将这些接口信息的Redis键添加到了排行榜中。
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/30 21:47
     */
    private void invokeRankInitial()
    {
        // 获取调用次数最多的接口信息
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(
                Math.toIntExact(RedisConstant.REDIS_INVOKE_RANK_MAX_SIZE));
        // 获取接口ID集合
        List<Long> interfaceInfoIdList = userInterfaceInfoList.stream()
                .map(UserInterfaceInfo::getInterfaceInfoId)
                .collect(Collectors.toList());
        // 获取接口详细信息
        Map<Long, InterfaceInfo> interfaceInfoMap = interfaceInfoMapper.selectBatchIds(interfaceInfoIdList)
                .stream()
                .collect(Collectors.toMap(InterfaceInfo::getId, info -> info));
        // 将接口信息存入redis排行榜
        userInterfaceInfoList.forEach(userInterfaceInfo -> {
            InterfaceInfo curInfo = interfaceInfoMap.get(userInterfaceInfo.getInterfaceInfoId());
            if (curInfo != null)
            {
                HashMap<String, Object> interfaceMap = new HashMap<>();
                interfaceMap.put("id", curInfo.getId());
                interfaceMap.put("name", curInfo.getName());
                interfaceMap.put("description", curInfo.getDescription());
                interfaceMap.put("totalNum", userInterfaceInfo.getTotalNum());
                // 把数据单独存在一个redis表里
                String infoKey = RedisConstant.REDIS_INVOKE_INFO_KEY + userInterfaceInfo.getId();
                redisOperatorService.setHashMap(infoKey, interfaceMap,null);
                // 将key存入排行榜，并把totalNum存入score
                redisOperatorService.zAdd(RedisConstant.REDIS_INVOKE_RANK_KEY, infoKey,
                        userInterfaceInfo.getTotalNum());
            }
        });
    }

}
