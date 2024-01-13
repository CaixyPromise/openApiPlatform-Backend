package com.caixy.backend.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EmailServiceImplTest
{
    @Resource
    private EmailServiceImpl emailService;

    @Test
    void sendCaptchaEmail()
    {
        emailService.sendCaptchaEmail("1944630344@qq.com", "1234");
    }
}