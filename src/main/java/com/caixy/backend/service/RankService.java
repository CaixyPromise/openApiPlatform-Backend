package com.caixy.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.entity.UserInterfaceInfo;
import com.caixy.backend.model.vo.InterfaceInvokeCountVO;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

/**
 * 排行榜功能服务类接口
 *
 * @name: com.caixy.project.service.RankService
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 21:58
 **/
public interface RankService extends IService<UserInterfaceInfo>
{
    boolean zAdd(String key, String value, double score);

    void zIncreamentScore(String key, String value, double score);

    long zGetRank(String key, String value);

    double zGetScore(String key, String value);

    Set<ZSetOperations.TypedTuple<String>> rankWithScore(String key);

    List<InterfaceInvokeCountVO> getTopInvokeInterfaceInfo(int topLimit);
}
