package com.caixy.backend.service.impl;

import com.caixy.backend.config.EmailConfig;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.service.EmailService;
import com.caixy.backend.utils.EmailUtils;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

/**
 * 邮箱服务类实现
 *
 * @name: com.caixy.backend.service.impl.EmailServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2024-01-10 22:01
 **/
public class EmailServiceImpl implements EmailService
{
    @Resource
    private JavaMailSender mailSender;

    @Resource
    private EmailConfig emailConfig;

    /**
     * 发送邮箱验证码
     *
     * @param targetEmailAccount 邮箱账号
     * @param captcha      验证码
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 2:04
     */
    @Override
    public void sendCaptchaEmail(String targetEmailAccount, String captcha)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮箱发送内容组成
        message.setSubject(EmailConstant.EMAIL_SUBJECT);
        message.setText(EmailUtils.buildCaptchaEmailContent(EmailConstant.EMAIL_HTML_CONTENT_PATH, captcha));
        message.setTo(targetEmailAccount);
        message.setFrom(EmailConstant.EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
        mailSender.send(message);

    }

    /**
     * 发送支付成功信息
     *
     * @param targetEmailAccount 邮箱账号
     * @param orderName    订单名称
     * @param orderTotal   订单金额
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 2:04
     */
    @Override
    public void sendPaymentSuccessEmail(String targetEmailAccount, String orderName, String orderTotal)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮箱发送内容组成
        message.setSubject("【" + EmailConstant.EMAIL_TITLE + "】感谢您的购买，请查收您的订单");
        message.setText(EmailUtils.buildPaySuccessEmailContent(EmailConstant.EMAIL_HTML_PAY_SUCCESS_PATH, orderName, orderTotal));
        message.setTo(targetEmailAccount);
        message.setFrom(EmailConstant.EMAIL_TITLE + '<' + emailConfig.getUsername() + '>');
        mailSender.send(message);
    }
}
