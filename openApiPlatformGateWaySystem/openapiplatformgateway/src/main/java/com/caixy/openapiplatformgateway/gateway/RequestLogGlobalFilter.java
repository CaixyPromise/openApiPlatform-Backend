package com.caixy.openapiplatformgateway.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Name: com.caixy.openapiplatformgateway.gateway.RequestLogGlobalFilter
 * @Description: 全局请求过滤器: 写响应日志
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 16:30
 **/
@Component
@Slf4j
public class RequestLogGlobalFilter implements GlobalFilter, Ordered
{
    private static final String INTERFACE_HOST = "http://localhost:8123";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        System.out.println("RequestLogGlobalFilter 执行");

        // 1. 写请求日志
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求路径
        String path = request.getPath().value().replaceFirst("^/api", "");;
        // 获取发起请求来源路径
        String originPath = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        // 获取请求标识
        String requestId = request.getId();
        // 获取请求方法
        String method = request.getMethodValue();

        exchange.getAttributes().put("path", path);
        exchange.getAttributes().put("method", method);
        log.info("请求日志: path={}, originPath={}, requestId={}, method={}", path, originPath, requestId, method);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return 1;
    }
}
