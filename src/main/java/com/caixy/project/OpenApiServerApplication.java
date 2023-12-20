package com.caixy.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caixy.project.mapper")
public class OpenApiServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(OpenApiServerApplication.class, args);
    }

}
