package com.caixy.backend.controller;

import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.RedisOperatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日签到接口
 *
 * @name: com.caixy.backend.controller.DailyCheckController
 * @author: CAIXYPROMISE
 * @since: 2024-01-12 16:31
 **/
@RestController
@RequestMapping("/dailyCheck")
public class DailyCheckController
{
    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private UserService userService;


    @PostMapping("/check")
    public BaseResponse<Boolean> tryDailyCheck(HttpServletRequest request)
    {
        UserVO curUser = userService.getLoginUser(request);
        return null;
    }
}
