package com.caixy.backend.config;

import com.caixy.backend.interceptor.RequestInterceptors;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * 注册请求拦截器
 *
 * @name: com.caixy.project.config.WebMvcInterceptorConfig
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 12:33
 **/
@Configuration
public class WebMvcInterceptorConfig extends WebMvcConfigurationSupport
{
    @Resource
    private RequestInterceptors requestInterceptors;


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(requestInterceptors)
                .addPathPatterns("/**");
    }
}
