package com.caixy.openapiplatforminterfacesystem.config;

import com.caixy.openapiplatforminterfacesystem.interator.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 请求配置类
 *
 * @name: com.caixy.openapiplatforminterfacesystem.config.WebConfigs
 * @author: CAIXYPROMISE
 * @since: 2023-12-27 14:05
 **/
@Configuration
public class WebConfigs implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new RequestInterceptor())
                .addPathPatterns("/**");// 拦截所有路径
    }

}
