package com.caixy.openapiplatforminterfacesystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实现调用的接口, 只负责接口逻辑，不做任何数据库逻辑
 *
 * @author CAIXYPROMISE
 * @version 1.0
 * @createdDate 2023/12/19 17:35
 * @updatedDate 2023/12/19 17:35
 */
@SpringBootApplication
public class OpenApiPlatformInterfaceSystemApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(OpenApiPlatformInterfaceSystemApplication.class, args);
    }
}
