package com.caixy.project.controller;

import com.caixy.project.annotation.AuthCheck;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.service.RankService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @PostMapping("/getInvoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<?> getInvokeRank()
    {

        return ResultUtils.success("getInvokeRank");
    }
}
