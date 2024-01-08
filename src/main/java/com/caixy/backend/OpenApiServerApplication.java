package com.caixy.backend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.caixy.backend.mapper")
@EnableDubbo
@EnableScheduling
public class OpenApiServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(OpenApiServerApplication.class, args);
    }

}
