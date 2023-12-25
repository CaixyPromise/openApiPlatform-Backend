package com.caixy.project;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caixy.project.mapper")
@EnableDubbo
public class OpenApiServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(OpenApiServerApplication.class, args);
    }

}
