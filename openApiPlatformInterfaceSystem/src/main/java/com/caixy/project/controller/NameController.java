package com.caixy.project.controller;

import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口控制器
 *
 * @name: com.caixy.project.controller.NameController
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 12:24
 **/
@RestController
@RequestMapping("/name")
public class NameController
{
    @GetMapping("/getName")
    public BaseResponse<String> getName()
    {
        return ResultUtils.success("caixy");
    }
}
