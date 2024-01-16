package com.caixy.project.controller;

import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.model.dto.Name.TestRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 测试接口控制器
 *
 * @name: com.caixy.project.controller.NameController
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 12:24
 **/
@RestController
@RequestMapping("/name")
@Slf4j
public class NameController
{
    @GetMapping("/getName")
    public BaseResponse<String> getName()
    {
        return ResultUtils.success("caixy");
    }

    @GetMapping("/test")
    public BaseResponse<String> test(@RequestParam String name,
                                     @RequestParam Integer age)
    {
        return ResultUtils.success("requestBody is, name:" + name + ", age: " + age);
    }

    @PostMapping("/test/post")
    public  BaseResponse<String> testPost(@RequestBody TestRequestBody requestBody)
    {
        String name = requestBody.getName();
        Integer age = requestBody.getAge();
        return ResultUtils.success("requestBody is, name:" + name + ", age: " + age);
    }
}
