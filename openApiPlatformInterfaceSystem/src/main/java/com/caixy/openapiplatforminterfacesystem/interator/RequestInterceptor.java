package com.caixy.openapiplatforminterfacesystem.interator;

import com.caixy.openapiplatforminterfacesystem.common.ErrorCode;
import com.caixy.openapiplatforminterfacesystem.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 请求拦截器，拦截没有经过网关染色的请求
 *
 * @name: com.caixy.openapiplatforminterfacesystem.interator.RequestInterceptor
 * @author: CAIXYPROMISE
 * @since: 2023-12-27 13:58
 **/
@Component
public class RequestInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException
    {
        String path = request.getRequestURI();
        System.out.println(path);
        //  获取请求头中的source字段
        String headerValue = request.getHeader("source");
        if (headerValue == null || !headerValue.equals("api-gateway"))
        {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write(ErrorCode.FORBIDDEN_ERROR.toString());
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }
        return true;
    }
}
