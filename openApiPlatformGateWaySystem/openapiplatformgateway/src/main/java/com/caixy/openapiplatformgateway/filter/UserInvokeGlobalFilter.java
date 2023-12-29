package com.caixy.openapiplatformgateway.filter;

import com.caixy.openapicommon.services.InnerInterfaceInfoService;
import com.caixy.openapicommon.services.InnerUserInterfaceInfoService;
import com.caixy.openapiplatformgateway.common.ErrorCode;
import com.caixy.openapiplatformgateway.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 用户调用功能过滤器
 *
 * @name: com.caixy.openapiplatformgateway.gateway.UserInvokeGlobalFilter
 * @author: CAIXYPROMISE
 * @since: 2023-12-21 22:42
 **/
@Slf4j
@Component
public class UserInvokeGlobalFilter implements GlobalFilter, Ordered
{
    @DubboReference
    private InnerUserInterfaceInfoService userInterfaceInfoService;
    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        log.info("UserInvokeGlobalFilter success executed");
        // 1.  获取请求参数中的用户ID和请求接口信息
        String method = String.valueOf(exchange.getAttributes().get("method"));
        String path = String.valueOf(exchange.getAttributes().get("path"));
        Long interfaceId = interfaceInfoService.getInterfaceId(path, method);
        Long userId = (Long) exchange.getAttributes().get("userId");

        if (interfaceId == null || userId == null)
        {
            throw new CustomException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2. 获取请求接口的数据是否还有资格调用
        boolean flag = userInterfaceInfoService.isAllowInvoke(interfaceId, userId);
        if (!flag)
        {
            throw new CustomException(ErrorCode.NO_AUTH_ERROR);
        }

        return handleResponse(exchange, chain, interfaceId, userId);
    }


    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceId, long userId)
    {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                log.info("HttpStatus is OK");

                // Decorating the response for additional capabilities
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse)
                {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body)
                    {
                        log.info("Writing response with Mono");
                        if (body instanceof Flux)
                        {
                            log.info("Writing response with Flux");
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            return super.writeWith(fluxBody.handle((dataBuffer, sink) -> {
                                try {
                                    userInterfaceInfoService.updateUserInvokeCount(interfaceId, userId, 1);
                                }
                                catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);
                                String data = new String(content, StandardCharsets.UTF_8);
                                log.info("Response result: " + data);
                                sink.next(bufferFactory.wrap(content));
                            }));
                        }
                        else {
                            log.error("Response code exception: {}", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };

                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);
        }
        catch (Exception e)
        {
            log.error("Gateway response handling exception", e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder()
    {
        return -2;
    }
}

/*
 package com.caixy.openapiplatformgateway.gateway;

 import com.caixy.openapicommon.models.entity.InterfaceInfo;
 import com.caixy.openapicommon.services.InnerInterfaceInfoService;
 import com.caixy.openapicommon.services.InnerUserInterfaceInfoService;
 import com.caixy.openapiplatformgateway.common.ErrorCode;
 import com.caixy.openapiplatformgateway.exception.CustomException;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.dubbo.config.annotation.DubboReference;
 import org.reactivestreams.Publisher;
 import org.springframework.cloud.gateway.filter.GatewayFilterChain;
 import org.springframework.cloud.gateway.filter.GlobalFilter;
 import org.springframework.core.Ordered;
 import org.springframework.core.io.buffer.DataBuffer;
 import org.springframework.core.io.buffer.DataBufferFactory;
 import org.springframework.core.io.buffer.DataBufferUtils;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.server.reactive.ServerHttpResponse;
 import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
 import org.springframework.stereotype.Component;
 import org.springframework.web.server.ServerWebExchange;
 import reactor.core.publisher.Flux;
 import reactor.core.publisher.Mono;

 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;


@Slf4j
@Component
public class UserInvokeGlobalFilter implements GlobalFilter, Ordered
{
    @DubboReference
    private InnerUserInterfaceInfoService userInterfaceInfoService;
    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
         log.info("UserInvokeGlobalFilter success executed");

        // 1. 获取请求参数中的用户ID和请求接口信息
        String method = String.valueOf(exchange.getAttributes().get("method"));
        String path = String.valueOf(exchange.getAttributes().get("path"));
        Long interfaceId = interfaceInfoService.getInterfaceId(path, method);
        Long userId = (Long) exchange.getAttributes().get("userId");

        if (interfaceId == null || userId == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2. 获取请求接口的数据是否还有资格调用
        boolean flag = userInterfaceInfoService.isAllowInvoke(interfaceId, userId);
        if (!flag) {
            throw new CustomException(ErrorCode.NO_AUTH_ERROR);
        }

        return chain.filter(exchange)
                .then(Mono.defer(() -> handleResponse(exchange, chain, interfaceId, userId)));
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
         log.info("handleResponsesuccess executed");
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                 log.info("writeWithsuccess executed");
                if (body instanceof Flux) {
                    Flux<DataBuffer> fluxBody = Flux.from(body);

                    return super.writeWith(fluxBody.buffer().flatMap(dataBuffers -> {
                        DataBuffer joinedDataBuffer = bufferFactory.join(dataBuffers);
                        byte[] content = new byte[joinedDataBuffer.readableByteCount()];
                        joinedDataBuffer.read(content);
                        DataBufferUtils.release(joinedDataBuffer);

                        // success executed后处理逻辑
                        postProcess(interfaceInfoId, userId, originalResponse.getStatusCode());

                        // 重新发送原始内容
                        DataBuffer buffer = bufferFactory.wrap(content);
                        return Flux.just(buffer);
                    }));
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private void postProcess(long interfaceInfoId, long userId, HttpStatus statusCode) {
        if (statusCode == HttpStatus.OK) {
            // 逻辑：调用成功时的处理
            try {
                userInterfaceInfoService.updateUserInvokeCount(interfaceInfoId, userId, 1);
                log.info("Successfully updated invoke count");
            } catch (Exception e) {
                log.error("invokeCount error", e);
            }
        } else {
            // 逻辑：处理非200响应
            log.error("Non-200 response: " + statusCode);
        }
    }

    @Override
    public int getOrder()
    {
        return 3;
    }
}
* */