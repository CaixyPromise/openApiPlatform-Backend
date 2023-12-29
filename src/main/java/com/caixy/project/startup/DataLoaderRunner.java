package com.caixy.project.startup;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import com.caixy.project.model.entity.InterfaceInfo;
import com.caixy.project.model.entity.UserInterfaceInfo;
import com.caixy.project.model.vo.InterfaceInvokeCountVO;
import com.caixy.project.service.InterfaceInfoService;
import com.caixy.project.utils.JsonUtils;
import com.caixy.project.utils.RedisOperatorService;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        // 获取调用次数最多的接口信息
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);

        // 将每个接口的信息添加到排行榜中
        for (UserInterfaceInfo userInfo : userInterfaceInfoList) {
            String value = JsonUtils.objectToString(userInfo); // 将对象转换为 JSON 字符串
            redisOperatorService.zAdd(RedisConstant.REDIS_INVOKE_RANK_KEY, value, userInfo.getTotalNum());
        }
    }
//        List<InterfaceInvokeCountVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
//            InterfaceInvokeCountVO interfaceInfoVO = new InterfaceInvokeCountVO();
//            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
//            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
//            interfaceInfoVO.setTotalNum(totalNum);
//            return interfaceInfoVO;
//        }).collect(Collectors.toList());
//        // 插入到redis的排行榜里
//        redisOperatorService.zAdd(RedisConstant.REDIS_INVOKE_RANK_KEY,interfaceInfoVOList);

}
