package com.caixy.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.yupi.project.mapper")
public class OpenApiServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(OpenApiServerApplication.class, args);
    }

}
