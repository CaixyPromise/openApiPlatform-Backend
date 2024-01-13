package com.caixy.backend.controller;

import cn.hutool.core.util.RandomUtil;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.constant.RedisConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.exception.ThrowUtils;
import com.caixy.backend.model.dto.captcha.ModifyEmailCaptchaRequest;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.EmailService;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.RedisOperatorService;
import com.caixy.backend.utils.RegexUtils;
import com.caixy.backend.utils.SafetyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final static int VERIFY_OLD_EMAIL = 0;
    private final static int VERIFY_NEW_EMAIL = 1;
    private final static int VERIFY_BIND_EMAIL = 5;
    private final static int VERIFY_MODIFY_PASSWORD = 99;

    /**
     * 获取验证码
     * <ol>
     *
     * @param emailCaptchaRequest 请求载荷，包含修改邮箱所需的信息。参见 {@link ModifyEmailCaptchaRequest}
     * @return {@link BaseResponse}<{@link String}> 调用是否成功
     * @step1: 用户登录验证：首先检查用户是否已登录并获取登录用户信息。
     * @step2: 根据eventType执行不同操作：
     * @step3: 邮箱验证和验证码发送：
     * <li>a. 在每个handle方法中，先验证邮箱的合法性。</li>
     * <li>b. 确保Redis中不存在已发送的验证码。</li>
     * <li>c. 生成新的验证码并发送到相应的邮箱地址。</li>
     * @step4: 返回生成的验证码签名：每个handle方法生成新的验证码签名，并将其作为响应返回给前端。
     * </ol>
     */
    @PostMapping("/captcha/modify/email")
    public BaseResponse<String> getModifyEmailCaptcha(@Valid @RequestBody ModifyEmailCaptchaRequest emailCaptchaRequest,
                                                      HttpServletRequest request)
    {
        // 1. 检查用户登录状态
        UserVO currentUser = userService.getLoginUser(request);
        User dbUser = userService.getById(currentUser.getId());

        // 2. 根据eventType进行不同的处理
        switch (emailCaptchaRequest.getEventType())
        {
            case VERIFY_OLD_EMAIL: // 修改邮箱-验证旧邮箱
                return handleVerifyOldEmail(dbUser, emailCaptchaRequest);
            case VERIFY_NEW_EMAIL: // 修改邮箱-验证新邮箱
                return handleVerifyNewEmail(dbUser, emailCaptchaRequest);
            case VERIFY_BIND_EMAIL: // 之前从未绑定邮箱
                return handleBindEmail(dbUser, emailCaptchaRequest);
            case VERIFY_MODIFY_PASSWORD:
                return handleModifyPassword(dbUser, emailCaptchaRequest);
            default:
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "非法操作");
        }
    }

    /**
     * 处理验证旧邮箱的逻辑。
     *
     * @step1. 获取当前用户的邮箱地址。
     * @step2. 验证邮箱是否合规，并检查Redis中是否已发送验证码。
     * @step3. 生成新验证码并发送到用户的旧邮箱。
     */
    private BaseResponse<String> handleVerifyOldEmail(User dbUser, ModifyEmailCaptchaRequest request)
    {
        ThrowUtils.throwIf(request.getEventType() != VERIFY_OLD_EMAIL, ErrorCode.OPERATION_ERROR, "非法操作");
        String emailAccount = dbUser.getEmail().trim();
        ThrowUtils.throwIf(dbUser.getEmail().trim().equals(request.getNewEmail()), ErrorCode.OPERATION_ERROR, "新邮箱不能与旧邮箱相同");
        validateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_CAPTCHA_CACHE_KEY);
        return generateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_CAPTCHA_CACHE_KEY);
    }

    /**
     * 处理验证新邮箱的逻辑。
     *
     * @step1. 从请求中获取新邮箱地址。
     * @step2. 验证新邮箱是否合规，并检查Redis中是否已发送验证码。
     * @step3. 生成新验证码并发送到用户的新邮箱。
     */
    private BaseResponse<String> handleVerifyNewEmail(User dbUser, ModifyEmailCaptchaRequest request)
    {
        ThrowUtils.throwIf(request.getEventType() != VERIFY_NEW_EMAIL, ErrorCode.OPERATION_ERROR, "非法操作");
        String emailAccount = request.getNewEmail().trim();
        validateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
        return generateAndSendCaptcha(emailAccount, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
    }

    /**
     * 处理绑定新邮箱的逻辑。
     *
     * @step1. 确保当前用户没有已绑定的邮箱。
     * @step2. 从请求中获取新邮箱地址。
     * @step3. 验证新邮箱是否合规，并检查Redis中是否已发送验证码。
     * @step4 校验用户账号密码
     * @step5. 生成新验证码并发送到用户的新邮箱。
     */
    private BaseResponse<String> handleBindEmail(User dbUser, ModifyEmailCaptchaRequest request)
    {
        ThrowUtils.throwIf(request.getEventType() != VERIFY_BIND_EMAIL, ErrorCode.OPERATION_ERROR, "非法操作");
        // 确保当前用户没有已绑定的邮箱
        if (dbUser.getEmail() != null && !dbUser.getEmail().trim().isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已绑定邮箱，不能使用此选项");
        }
        String newEmail = request.getNewEmail().trim();
        validateAndSendCaptcha(newEmail, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
        // 绑定邮箱需要验证密码
        User user = userService.verifyUserPassword(dbUser.getUserAccount(), request.getSignature());
        ThrowUtils.throwIf(user == null, ErrorCode.OPERATION_ERROR, "用户密码错误");
        return generateAndSendCaptcha(newEmail, EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY);
    }

    /**
     * 处理修改密码的邮箱验证码发送逻辑
     *
     * @step1: 确认当前用户已绑定的邮箱;
     * @step2: 校验随机数与时间戳是否合规
     * @step3: 生成并发送验证码到用户绑定的邮箱
     * @author CAIXYPROMISE
     * @version a
     * @since 2024/1/2 18:21
     */
    private BaseResponse<String> handleModifyPassword(User dbUser, ModifyEmailCaptchaRequest request)
    {
        ThrowUtils.throwIf(request.getEventType() != VERIFY_MODIFY_PASSWORD, ErrorCode.OPERATION_ERROR, "非法操作");
        // 校验随机数与时间戳
        String nonce = request.getCode();
        String timestamp = request.getSignature();
        // 检查时间随机树是否符合规则
        SafetyUtils.checkTimestampAndRandom(nonce, RedisConstant.MODIFY_PASSWORD_NONCE_KEY,
                Long.parseLong(timestamp), RedisConstant.MODIFY_PASSWORD_NONCE_EXPIRE,
                redisOperatorService);
        String newEmail = dbUser.getEmail();
        validateAndSendCaptcha(newEmail, EmailConstant.EMAIL_MODIFY_PASSWORD_CACHE_KEY);
        BaseResponse<String> result = generateAndSendCaptcha(newEmail, EmailConstant.EMAIL_MODIFY_PASSWORD_CACHE_KEY);
        // 把随机数存入redis
        redisOperatorService.setString(RedisConstant.MODIFY_PASSWORD_NONCE_KEY + nonce, "1", RedisConstant.MODIFY_PASSWORD_NONCE_EXPIRE);
        return result;
    }

    private void validateAndSendCaptcha(String email, String redisKey)
    {
        // 校验邮箱是否合规
        ThrowUtils.throwIf(!validateEmail(email), ErrorCode.PARAMS_ERROR, "邮箱不合法");
        // 检查Redis中是否存在该邮箱
        if (redisOperatorService.hasKey(redisKey + email))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已发送验证码，请稍后再试");
        }
    }

    /**
     * 生成并发送验证码
     * 存入redis内容: key: redis分类键+邮箱 {
     * 签名: 验证码
     * }
     *
     * @author CAIXYPROMISE
     * @version a
     * @since 2024/112 18:51
     */
    private BaseResponse<String> generateAndSendCaptcha(String email, String redisKey)
    {
        String captcha = RandomUtil.randomNumbers(6);
        String signCode = UUID.randomUUID().toString();
        HashMap<String, String> emailCodeMap = new HashMap<>();
        emailCodeMap.put(signCode, captcha);
        redisOperatorService.setStringHashMap(redisKey + email, emailCodeMap, EmailConstant.EMAIL_CAPTCHA_CACHE_TTL);
        // 放入线程池发送邮件->
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
