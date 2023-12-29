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
import reactor.core.publisher.SynchronousSink;

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
        try
        {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK)
            {
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
                            return super.writeWith(fluxBody.handle((dataBuffer, sink) ->
                            {
                                // 做调用统计
                                postProcess(interfaceId, userId);
                                // 写入请求结果数据
                                writeResponse(dataBuffer, sink, bufferFactory);
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
        } catch (Exception e)
        {
            log.error("Gateway response handling exception", e);
            return chain.filter(exchange);
        }
    }

    /**
     * 做接口调用统计
     *
     * @author CAIXYPROMISE
     * @param userId 请求的用户Id
     * @param interfaceInfoId 接口信息id
     * @version 1.0
     * @since 2023/12/29 16:22
     */
    private void postProcess(long interfaceInfoId, long userId)
    {
        // 逻辑：调用成功时的处理
        try {
            userInterfaceInfoService.updateUserInvokeCount(interfaceInfoId, userId, -1);
            log.info("Successfully updated invoke count");
        }
        catch (Exception e) {
            log.error("invokeCount error", e);
        }

    }

    /**
     * 这个方法主要用于在响应被发送回客户端之前处理和记录响应数据。
     * 从 DataBuffer 中提取响应内容并记录，然后重新包装并写回响应流。
     * @param dataBuffer 包含响应数据的 DataBuffer。DataBuffer 是一个反应式数据缓冲区，用于存储字节数据。
     * @param sink       用于发送数据的反应式编程构件。它允许我们将处理过的数据重新发送回响应流。
     * @param bufferFactory 用于创建新的 DataBuffer 实例。由于原始的 DataBuffer 在读取后会被释放，所以需要创建新的 DataBuffer 来包装处理后的数据。
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/29 16:25
     */
    private void writeResponse(DataBuffer dataBuffer,
                               SynchronousSink<DataBuffer> sink,
                               DataBufferFactory bufferFactory)
    {
        // 从 DataBuffer 中读取响应内容。DataBuffer 是一个数据缓冲区，用于存储字节数据。
        byte[] content = new byte[dataBuffer.readableByteCount()];
        // 将 DataBuffer 中的数据读入 byte 数组。这里，我们读取的是整个缓冲区的数据。
        dataBuffer.read(content);

        // 释放 DataBuffer 资源。这是重要的清理步骤，用于避免内存泄漏。
        DataBufferUtils.release(dataBuffer);

        // 将 byte 数组转换为字符串。这里，我们使用 UTF-8 编码将字节数据转换为字符串形式，
        // 以便于记录和处理文本数据。
        String data = new String(content, StandardCharsets.UTF_8);

        log.info("Response result: " + data);

        // 将原始内容重新包装到新的 DataBuffer 中，以便可以将其写回响应流。因为原始的 DataBuffer 已经被读取和释放了。
        sink.next(bufferFactory.wrap(content));
    }

    @Override
    public int getOrder()
    {
        return -2;
    }
}
