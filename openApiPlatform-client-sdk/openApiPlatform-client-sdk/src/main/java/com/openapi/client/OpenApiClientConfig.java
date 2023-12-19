package com.openapi.client;
import com.openapi.client.client.OpenApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("openapi.client")
@Data
@ComponentScan
public class OpenApiClientConfig
{
    private String accessKey;
    private String secretKey;

    /**
    * @Description: 
    * @Param: 
    * @return: 
    * @Author: CAIXYPROMISE
    * @Date: 19:26
    */
    @Bean
    public OpenApiClient openApiClient()
    {
        return new OpenApiClient(accessKey, secretKey);
    }

}
