package com.caixy.backend.controller;

import cn.hutool.core.util.RandomUtil;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.config.EmailConfig;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.utils.EmailUtils;
import com.caixy.backend.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * 邮箱操作控制器
 *
 * @name: com.caixy.backend.controller.EmailController
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 21:42
 **/
@RestController
@RequestMapping("/email")
@Slf4j
public class EmailController
{
    @Resource
    private EmailConfig emailConfig;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private RedisOperatorService redisOperatorService;



    /**
     * 获取验证码
     *
     * @param emailAccount 电子邮件帐户
     * @return {@link BaseResponse}<{@link String}>
     */
    @GetMapping("/captcha")
    public BaseResponse<Boolean> getCaptcha(String emailAccount)
    {
        if (StringUtils.isBlank(emailAccount))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String captcha = RandomUtil.randomNumbers(6);
        try {
            redisOperatorService.setString(EmailConstant.EMAIL_CAPTCHA_CACHE_KEY + emailAccount, captcha, (5L * 60L));
            sendCaptchaEmail(emailAccount, captcha);
            return ResultUtils.success(true);
        }
        catch (Exception e) {
            log.error("【发送验证码失败】" + e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码获取失败");
        }
    }

    private void sendCaptchaEmail(String emailAccount, String captcha)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮箱发送内容组成
        message.setSubject(EmailConstant.EMAIL_SUBJECT);
        message.setText(EmailUtils.buildCaptchaEmailContent(EmailConstant.EMAIL_HTML_CONTENT_PATH, captcha));
        message.setTo(emailAccount);
        message.setFrom(EmailConstant.EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
        mailSender.send(message);
    }
}
