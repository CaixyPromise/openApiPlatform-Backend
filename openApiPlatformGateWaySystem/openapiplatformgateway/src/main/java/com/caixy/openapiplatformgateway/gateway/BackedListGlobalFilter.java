package com.caixy.openapiplatformgateway.gateway;

import com.caixy.openapicommon.services.InnerBackedListService;
import com.caixy.openapiplatformgateway.common.ErrorCode;
import com.caixy.openapiplatformgateway.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 全局黑名单过滤器设计
 *
 * @name: com.caixy.openapiplatformgateway.gateway.BackedListGlobalFilter
 * @author: CAIXYPROMISE
 * @since: 2023-12-21 22:00
 **/
@Component
@Slf4j
public class BackedListGlobalFilter implements GlobalFilter, Ordered
{
    @DubboReference
    private InnerBackedListService innerBackedListService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        System.out.println("BackedListGlobalFilter 执行");
        String remoteIp = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        if (innerBackedListService.isInsideBlackList(remoteIp))
        {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR, "IP:" + remoteIp + " is in black list");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
}
