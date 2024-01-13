package com.caixy.backend.service.impl;

import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.config.EmailConfig;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.service.EmailService;
import com.caixy.backend.utils.EmailUtils;
import com.caixy.backend.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 邮箱服务类实现
 *
 * @name: com.caixy.backend.service.impl.EmailServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:01
 **/
@Service
@Slf4j
public class EmailServiceImpl implements EmailService
{
    @Resource
    private JavaMailSender mailSender;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private EmailConfig emailConfig;


    /**
     * 发送邮箱验证码
     *
     * @param targetEmailAccount 邮箱账号
     * @param captcha            验证码
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 2:04
     */
    @Override
    public void sendCaptchaEmail(String targetEmailAccount, String captcha)
    {
        threadPoolExecutor.submit(() ->
        {
            try {
                // 发送邮件逻辑
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
//        SimpleMailMessage message = new SimpleMailMessage();
                // 邮箱发送内容组成
                helper.setSubject(EmailConstant.EMAIL_SUBJECT);
                helper.setText(EmailUtils.buildCaptchaEmailContent(EmailConstant.EMAIL_HTML_CONTENT_PATH, captcha), true);
                helper.setTo(targetEmailAccount);
                helper.setFrom(EmailConstant.EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
                mailSender.send(helper.getMimeMessage());
            }
            catch (MessagingException e)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码发送失败");
            }
        });
    }

    /**
     * 发送支付成功信息
     *
     * @param targetEmailAccount 邮箱账号
     * @param orderName          订单名称
     * @param orderTotal         订单金额
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 2:04
     */
    @Override
    public void sendPaymentSuccessEmail(String targetEmailAccount, String orderName, String orderTotal)
    {
        threadPoolExecutor.submit(() ->
        {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                // 邮箱发送内容组成
                helper.setSubject("【" + EmailConstant.EMAIL_TITLE + "】感谢您的购买，请查收您的订单");
                helper.setText(EmailUtils.buildPaySuccessEmailContent(EmailConstant.EMAIL_HTML_PAY_SUCCESS_PATH, orderName, orderTotal), true);
                helper.setTo(targetEmailAccount);
                helper.setFrom(EmailConstant.EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
                mailSender.send(message);
            }
            catch (MessagingException e)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮件发送失败");
            }
        });
    }
}
