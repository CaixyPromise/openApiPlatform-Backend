package com.caixy.backend.controller;

import com.caixy.backend.annotation.AuthCheck;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.mapper.UserInterfaceInfoMapper;
import com.caixy.backend.model.vo.InterfaceInvokeCountVO;
import com.caixy.backend.service.InterfaceInfoService;
import com.caixy.backend.service.RankService;
import com.caixy.backend.utils.RedisOperatorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
