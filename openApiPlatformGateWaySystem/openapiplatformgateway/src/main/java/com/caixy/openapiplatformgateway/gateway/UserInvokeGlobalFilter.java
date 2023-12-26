package com.caixy.openapiplatformgateway.gateway;

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
        System.out.println("UserInvokeGlobalFilter 执行");

        String method = String.valueOf(exchange.getAttributes().get("method"));
        String path = String.valueOf(exchange.getAttributes().get("path"));
        Long interfaceId = interfaceInfoService.getInterfaceId(path, method);
        Long userId = (Long) exchange.getAttributes().get("userId");

        if (interfaceId == null || userId == null)
        {
           throw new CustomException(ErrorCode.NOT_FOUND_ERROR);
        }
        return handleResponse(exchange, chain, interfaceId, userId);
    }

    @Override
    public int getOrder()
    {
        return 3;
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId)
    {
        try
        {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK)
            {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse)
                {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body)
                    {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux)
                        {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.handle((dataBuffer, sink) -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try
                                        {
                                            userInterfaceInfoService.isAllowInvoke(interfaceInfoId, userId);
                                        } catch (Exception e)
                                        {
                                            log.error("invokeCount error", e);
                                            sink.error(new CustomException(ErrorCode.OPERATION_ERROR));
                                            return;
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        sink.next(bufferFactory.wrap(content));
                                    }));
                        }
                        else
                        {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e)
        {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}