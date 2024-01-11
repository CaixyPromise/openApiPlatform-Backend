package com.caixy.backend.controller;

import cn.hutool.core.util.RandomUtil;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.exception.ThrowUtils;
import com.caixy.backend.model.dto.captcha.ModifyEmailCaptchaRequest;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.EmailService;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.RedisOperatorService;
import com.caixy.backend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;


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
    private EmailService emailService;

    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private UserService userService;


    /**
     * 获取验证码
     *
     * @param emailCaptchaRequest 电子邮件帐户
     * @return {@link BaseResponse}<{@link String}> 调用是否成功
     */
    @GetMapping("/captcha/modify/email")
    public BaseResponse<String> getCaptcha(@Valid @RequestBody ModifyEmailCaptchaRequest emailCaptchaRequest,
                                           HttpServletRequest request)
    {
        // 1. 检查用户登录状态
        UserVO currentUser = userService.getLoginUser(request);
        User dbUser = userService.getById(currentUser.getId());

        // 2. 根据eventType进行不同的处理
        switch (emailCaptchaRequest.getEventType())
        {
            case 0: // 修改邮箱
                return handleEventTypeZero(dbUser, emailCaptchaRequest);
            case 1: // 修改邮箱-验证新邮箱
                return handleEventTypeOne(dbUser, emailCaptchaRequest);
            case 3: // 之前从未绑定邮箱
                return handleEventTypeThree(dbUser, emailCaptchaRequest);
            default:
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "非法操作");
        }
    }

    private BaseResponse<String> handleEventTypeZero(User dbUser, ModifyEmailCaptchaRequest request)
    {
        String emailAccount = dbUser.getEmail().trim();
        validateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_CAPTCHA_CACHE_KEY);
        return generateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_CAPTCHA_CACHE_KEY);
    }

    private BaseResponse<String> handleEventTypeOne(User dbUser, ModifyEmailCaptchaRequest request)
    {
        String emailAccount = request.getNewEmail().trim();
        validateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
        return generateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
    }

    private BaseResponse<String> handleEventTypeThree(User dbUser, ModifyEmailCaptchaRequest request)
    {
        // 确保当前用户没有已绑定的邮箱
        if (dbUser.getEmail() != null && !dbUser.getEmail().trim().isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已绑定邮箱，不能使用此选项");
        }
        String newEmail = request.getNewEmail().trim();
        validateAndSendCaptcha(newEmail, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
        return generateAndSendCaptcha(newEmail, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
    }

    private void validateAndSendCaptcha(String email, String redisKey)
    {
        // 校验邮箱是否合规
        ThrowUtils.throwIf(!validateEmail(email), ErrorCode.PARAMS_ERROR, "邮箱不合法");
        // 检查Redis中是否存在该邮箱
        if (redisOperatorService.isExistKey(redisKey + email))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已发送验证码，请稍后再试");
        }
    }

    private BaseResponse<String> generateAndSendCaptcha(String email, String redisKey)
    {
        String captcha = RandomUtil.randomNumbers(6);
        String signCode = UUID.randomUUID().toString();
        HashMap<String, String> emailCodeMap = new HashMap<>();
        emailCodeMap.put(signCode, captcha);
        redisOperatorService.setStringHashMap(redisKey + email, emailCodeMap, EmailConstant.EMAIL_CAPTCHA_CACHE_TTL);
        emailService.sendCaptchaEmail(email, captcha);
        return ResultUtils.success(signCode);
    }

    /**
     * 校验邮箱是否合法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/11 1:15
     */
    private boolean validateEmail(String email)
    {
        return email != null && !email.isEmpty() && RegexUtils.isEmail(email);
    }
}
