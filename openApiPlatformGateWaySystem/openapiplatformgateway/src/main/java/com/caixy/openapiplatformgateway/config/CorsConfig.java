package com.caixy.openapiplatformgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collections;

/**
 * @name: com.caixy.openapiplatformgateway.config.CorsConfig
 * @author: CAIXYPROMISE
 * @since: 2024-02-03 16:35
 **/
@Configuration
public class CorsConfig
{
    /**
     * 创建一个CorsWebFilter的Bean，用于处理跨域请求。
     *
     * @return 返回配置好的CorsWebFilter。
     */
    @Bean
    public CorsWebFilter corsFilter()
    {
        // 设置创建CORS配置实例
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许所有请求头
        config.addAllowedHeader("*");
        // 设置允许发送Cookie
        config.setAllowCredentials(true);
        // 设置允许来自所有源的请求 todo: 设置成前端的地址，或者上线的地址，这里默认放通所有来源请求
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 设置允许所有HTTP方法
        config.addAllowedMethod("*");

        // 创建基于URL的CORS配置源，使用PathPatternParser进行路径匹配
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        // 注册CORS配置应用于所有路径
        source.registerCorsConfiguration("/**", config);

        // 返回新的CorsWebFilter实例，基于spring-cloud-gateway反应式编程实例
        return new CorsWebFilter(source);
    }
}
