package com.caixy.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import com.caixy.project.model.entity.InterfaceInfo;
import com.caixy.project.model.entity.UserInterfaceInfo;
import com.caixy.project.model.vo.InterfaceInvokeCountVO;
import com.caixy.project.service.InterfaceInfoService;
import com.caixy.project.service.RankService;
import com.caixy.project.utils.RedisOperatorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排行榜功能服务类的实现
 *
 * @name: com.caixy.project.service.impl.RankServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 21:57
 **/
@Service
@AllArgsConstructor
public class RankServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements RankService
{
    private final RedisOperatorService redisOperatorService;
    private final UserInterfaceInfoMapper userInterfaceInfoMapper;
    private final InterfaceInfoService interfaceInfoService;

    /**
     * 获取调用次数最多的接口信息
     * @param topLimit 排名大小
     * @return 调用次数最多前topLimit的接口信息
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/31 14:59
     */
    public List<InterfaceInvokeCountVO> getTopInvokeInterfaceInfo(int topLimit)
    {
        // 1. 获取调用次数最多的接口信息
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(topLimit);
        // 2. 获取接口信息列表的id
        Set<Long> interfaceIds = userInterfaceInfoList.stream()
                .map(UserInterfaceInfo::getInterfaceInfoId)
                .collect(Collectors.toSet());
        // 3. 根据id获取接口信息
        List<InterfaceInfo> interfaceInfos = interfaceInfoService.getInterfaceInfosByIds(interfaceIds);
        // 4. 映射信息，封装成VO
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        // 5. 封装成VO
        return interfaceInfos.stream().map(interfaceInfo -> {
            InterfaceInvokeCountVO interfaceInfoVO = new InterfaceInvokeCountVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 向排行榜添加用户和其对应的分数。
     *
     * @param key   排行榜的键。
     * @param value 用户标识，通常为用户ID。
     * @param score 用户的分数。
     * @return true 如果添加成功，否则 false。
     * @author CAIXYPROMISE
     * @since 2023-12-29
     */
    @Override
    public boolean zAdd(String key, String value, double score)
    {
        validateInput(key, value);
        try
        {
            return redisOperatorService.zAdd(key, value, score);
        } catch (Exception e)
        {
            // Log exception
            return false;
        }
    }

    /**
     * 增加用户的积分。
     *
     * @param key   排行榜的键。
     * @param value 用户标识，通常为用户ID。
     * @param score 要增加的分数。
     * @author CAIXYPROMISE
     * @since 2023-12-29
     */
    @Override
    public void zIncreamentScore(String key, String value, double score)
    {
        validateInput(key, value);
        try
        {
            redisOperatorService.zIncreamentScore(key, value, score);
        } catch (Exception e)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 获取用户在排行榜中的排名。
     *
     * @param key   排行榜的键。
     * @param value 用户标识，通常为用户ID。
     * @return 用户的排名。如果用户不存在，则返回-1。
     * @author CAIXYPROMISE
     * @since 2023-12-29
     */
    @Override
    public long zGetRank(String key, String value)
    {
        validateInput(key, value);
        try
        {
            Long rank = redisOperatorService.zGetRank(key, value);
            return rank != null ? rank : -1;
        } catch (Exception e)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 获取用户在排行榜中的分数。
     *
     * @param key   排行榜的键。
     * @param value 用户标识，通常为用户ID。
     * @return 用户的分数。如果用户不存在，则返回0。
     * @author CAIXYPROMISE
     * @since 2023-12-29
     */
    @Override
    public double zGetScore(String key, String value)
    {
        validateInput(key, value);
        try
        {
            Double score = redisOperatorService.zGetScoreByValue(key, value);
            return score;
        } catch (Exception e)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 获取排行榜，按照分数从高到低排序。
     *
     * @param key 排行榜的键。
     * @return 排行榜中的用户及其分数。如果排行榜为空，则返回空集合。
     * @author CAIXYPROMISE
     * @since 2023-12-29
     */
    @Override
    public Set<ZSetOperations.TypedTuple<String>> rankWithScore(String key)
    {
        if (StringUtils.isEmpty(key))
        {
            return Collections.emptySet();
        }
        try
        {
            return redisOperatorService.zReverseRangeWithScore(key);
        } catch (Exception e)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 验证输入参数是否合法。
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/9 22:25
     */
    private void validateInput(String key, String value)
    {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }
}