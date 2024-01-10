package com.caixy.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 邮箱配置
 *
 * @name: com.caixy.backend.config.EmailConfig
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 21:16
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailConfig
{
    private String host;
    private Integer port;
    private String username;
    private String defaultEncoding;

    @Bean
    public JavaMailSender javaMailSender()
    {
        return new JavaMailSenderImpl();
    }
}
