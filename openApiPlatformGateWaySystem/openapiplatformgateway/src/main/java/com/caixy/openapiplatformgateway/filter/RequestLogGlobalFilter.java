package com.caixy.openapiplatformgateway.filter;

import com.caixy.openapiplatformgateway.constants.UrlConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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
        log.info("RequestLogGlobalFilter success executed");

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
        // 获取请求来源header标志
        String source = request.getHeaders().getFirst(UrlConstants.HEADER_KEY_SOURCE);
        // 如果请求头不是来资源openapi-client的, 则拦截
        if (source != null && !source.equals(UrlConstants.HEADER_VALUE_SOURCE))
        {
            // 响应日志
            log.info("请求日志: 请求来源不是openapi-client, 请求被拦截, originPath={}", originPath);
            // 返回响应
            ServerHttpResponse response = exchange.getResponse();
            // 设置响应体，可以使用一个简单的字符串或更复杂的对象
            byte[] bytes = "{\"error\":\"Access Denied\"}".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            // 设置响应码
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.writeWith(Mono.just(buffer));
        }

        exchange.getAttributes().put("path", path);
        exchange.getAttributes().put("method", method);
        log.info("请求日志: path={}, originPath={}, requestId={}, method={}", path, originPath, requestId, method);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return -4;
    }
}
