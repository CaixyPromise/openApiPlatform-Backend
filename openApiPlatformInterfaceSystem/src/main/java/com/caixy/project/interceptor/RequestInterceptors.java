package com.caixy.project.interceptor;

import com.caixy.project.common.ErrorCode;
import com.caixy.project.exception.BusinessException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求拦截器
 *
 * @name: com.caixy.project.interator.RequestInterceptors
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 12:26
 **/
@Component
@Slf4j
public class RequestInterceptors implements HandlerInterceptor
{
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception
    {
        // 请求拦截逻辑- 拦截一切不是来自网关的请求
        // 获取请求头
        String requestHeader = request.getHeader("source");
        log.info("RequestInterceptors preHandle requestHeader: {}", requestHeader);
        // 判断请求头是否为网关
        if (requestHeader == null || !requestHeader.equals("api-gateway"))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
            // 拦截请求，不再继续执行
        }
        // 放行请求
        return true;
    }
}
