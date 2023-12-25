package com.caixy.openapiplatformgateway.gateway;

import com.caixy.openapicommon.model.entity.RequestUserInfo;
import com.caixy.openapicommon.services.InnerUserInfoService;
import com.caixy.openapiplatformgateway.common.ErrorCode;
import com.caixy.openapiplatformgateway.exception.CustomException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 校验用户信息全局过滤器
 *
 * @name: com.caixy.openapiplatformgateway.gateway.ValidateUserGlobalFilter
 * @author: CAIXYPROMISE
 * @since: 2023-12-21 22:27
 **/
@Component
public class ValidateUserGlobalFilter implements GlobalFilter, Ordered
{
    @DubboReference
    private InnerUserInfoService innerUserInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        // 1. 用户鉴权（判断 ak、sk 是否合法）
        ServerHttpRequest request = exchange.getRequest();
        RequestUserInfo requestUserInfo = getRequestUserInfo(request);
        Long userId = innerUserInfoService.verifyUserKey(requestUserInfo);
        if (userId.equals(-1L)) // userId == -1 表示用户鉴权失败
        {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }
        exchange.getAttributes().put("userId", userId);
        return chain.filter(exchange);
    }

    private static RequestUserInfo getRequestUserInfo(ServerHttpRequest request)
    {
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // 2. 填入数据
        RequestUserInfo requestUserInfo = new RequestUserInfo();
        requestUserInfo.setAccessKey(accessKey);
        requestUserInfo.setNonce(nonce);
        requestUserInfo.setTimestamp(timestamp);
        requestUserInfo.setSecretKey(sign);
        return requestUserInfo;
    }

    @Override
    public int getOrder()
    {
        return 2;
    }
}
