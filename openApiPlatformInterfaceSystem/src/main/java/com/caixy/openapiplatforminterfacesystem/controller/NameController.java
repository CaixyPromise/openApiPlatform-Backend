package com.caixy.openapiplatforminterfacesystem.controller;

import com.caixy.openapiplatforminterfacesystem.common.BaseResponse;
import com.caixy.openapiplatforminterfacesystem.common.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Name: com.openapi.openapiplatforminterfacesystem.controller.NameController
 * @Description: 模拟接口：姓名接口
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 18:03
 **/
@RestController
@RequestMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
public class NameController
{
    // 模拟接口：姓名接口
    // 返回一个字符串，表示一个人的姓名
    @GetMapping("/getName")
    public BaseResponse<?> getUserName(HttpServletRequest request)
    {
        String acceptHeader = request.getHeader("Accept");
        System.out.println("Accept Header: " + acceptHeader);
//        System.out.println("receive name: "+ );
        return ResponseUtils.success("caixy");
    }
}
