package com.caixy.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caixy.project.annotation.AuthCheck;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.common.ResultUtils;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排行榜接口控制器
 *
 * @name: com.caixy.project.controller.RankController
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 22:00
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/rank")
public class RankController
{
    private RankService rankService;
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    private InterfaceInfoService interfaceInfoService;
    private RedisOperatorService redisOperatorService;

    /**
     * 获取调用数据的排行榜信息
     * 需要管理员权限
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/30 22:40
     */
    @PostMapping("/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInvokeCountVO>> getInvokeRank()
    {
        return ResultUtils.success(rankService.getTopInvokeInterfaceInfo(10));
    }
}
