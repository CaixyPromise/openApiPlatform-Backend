package com.caixy.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.project.manager.RedisLimiterManager;
import com.caixy.project.model.dto.interfaceinfo.*;
import com.caixy.project.annotation.AuthCheck;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.DeleteRequest;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.constant.CommonConstant;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.model.entity.InterfaceInfo;
import com.caixy.project.model.vo.UserVO;
import com.caixy.project.service.InterfaceInfoService;
import com.caixy.project.service.UserService;
import com.caixy.project.utils.JsonUtils;
import com.openapi.client.client.OpenApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController
{

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request)
    {
        if (interfaceInfoAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        log.info("Add interfaceInfo:{}", interfaceInfoAddRequest);
        log.info("CopyProperties interfaceInfo:{}", interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfoAddRequest);
        UserVO loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        interfaceInfo.setRequestHeader(JsonUtils.objectToString(interfaceInfoAddRequest.getRequestHeader()));
        interfaceInfo.setRequestPayload(JsonUtils.objectToString(interfaceInfoAddRequest.getRequestPayload()));
        interfaceInfo.setResponseHeader(JsonUtils.objectToString(interfaceInfoAddRequest.getResponseHeader()));
        interfaceInfo.setResponsePayload(JsonUtils.objectToString(interfaceInfoAddRequest.getResponsePayload()));
        log.info("copy result is: {}", interfaceInfo);
//        UserVO loginUser = userService.getLoginUser(request);
//        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return ResultUtils.success("添加接口信息成功");
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody
                                                     DeleteRequest deleteRequest,
                                                     HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && userService.isAdmin(request))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request)
    {
        System.out.println(interfaceInfoUpdateRequest);
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
//        interfaceInfoService.validInterfaceInfo(interfaceInfo);
        UserVO user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && userService.isAdmin(request))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        log.info("Get interface info by id: {}", interfaceInfo);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest)
    {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null)
        {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request)
    {
        if (interfaceInfoQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String content = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 接口上线操作
     *
     * @param interfaceInfoOnlineRequest
     * @param request
     * @return handle result
    * */
    @PostMapping("/online")
    public BaseResponse<?> InterfaceOnline(@RequestBody InterfaceInfoOnLineRequest interfaceInfoOnlineRequest,
           HttpServletRequest request)
    {
        if (interfaceInfoOnlineRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return interfaceInfoService.InterfaceOnline(interfaceInfoOnlineRequest, request);
    }

    @PostMapping("/offline")
    public BaseResponse<?> InterfaceOffline(@RequestBody InterfaceInfoOffLineRequest interfaceInfoOffLineRequest,
           HttpServletRequest request)
    {
        if (interfaceInfoOffLineRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return interfaceInfoService.InterfaceOffline(interfaceInfoOffLineRequest, request);
    }

    /**
     * 接口模拟调用逻辑
     * @param interfaceInfoInvokeRequest 需要模拟请求的接口
     * @param request 请求载体
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/1226 16:22
     */
    @PostMapping("/invoke")
    public BaseResponse<?> tryToInterfaceInvoke(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                           HttpServletRequest request) throws UnsupportedEncodingException
    {
        // 1. 获取用户信息，并且判断是否登录
        UserVO currentUser = userService.getLoginUser(request);
        // 2. 获取接口信息
        InterfaceInfo interfaceInfo = interfaceInfoService.getInterfaceInfo(interfaceInfoInvokeRequest.getId());
        // 3. 判断接口是否可用
        if (interfaceInfo.getStatus().equals(0))    // 接口状态（0-关闭，1-开启）
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已关闭或不存在");
        }
        log.info("UserName is: {}", currentUser);
        // 4. 模拟请求
        OpenApiClient apiClient = new OpenApiClient(currentUser.getAccessKey(), currentUser.getSecretKey());
        log.info("apiClient userInfo is: {}", currentUser);

        try {
            redisLimiterManager.doRateLimiter(String.valueOf(currentUser.getId()));
            HashMap<String, Object> payloadObject = JsonUtils.jsonToMap(interfaceInfoInvokeRequest.getUserRequestPayload());
            String result = apiClient.makeRequest(interfaceInfo.getUrl(), interfaceInfo.getMethod(), payloadObject);
            return ResultUtils.success(result);
        }
        catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }
}
