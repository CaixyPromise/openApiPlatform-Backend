package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.constant.RedisConstant;
import com.caixy.backend.constant.UserConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.mapper.UserMapper;
import com.caixy.backend.model.dto.user.UserLoginRequest;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.EncryptionUtils;
import com.caixy.backend.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 用户服务实现类
 *
 * @author caixy
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService
{

    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private EncryptionUtils encryptionUtils;

    /**
     * 盐值，混淆密码
     */
    @Value("${encryption.key}")
    private String SALT;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String captchaId, String captchaCode)
    {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 12)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不符合要求");
        }
        if (userPassword.length() < 8 || userPassword.length() > 20
                || checkPassword.length() < 8 || checkPassword.length() > 20)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度不符合要求");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (!validatedCaptchaCode(captchaId, captchaCode))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        synchronized (userAccount.intern())
        {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = encryptionUtils.encodePassword(userPassword);
            // 3. 分配accessKey和secretKey
            String accessKey = encryptionUtils.makeUserKey(userAccount);
            String secretKey = encryptionUtils.makeUserKey(userAccount + "." + SALT);
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request)
    {
        // 0. 提取参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String captcha = userLoginRequest.getCaptcha().trim();
        String captchaId = userLoginRequest.getCaptchaId();
        // 1. 校验
        // 1.1 检查参数是否完整
        if (StringUtils.isAnyBlank(userAccount, userPassword, captcha, captchaId))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        // 1.2 校验验证码
        if (!validatedCaptchaCode(captchaId, captcha))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        // 验证用户账号密码
        User user = verifyUserPassword(userAccount, userPassword);
        if (user == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        // 3. 记录用户的登录态
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(user, userVo);
        log.info("登录用户:{}", userVo);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userVo);
        // 登录成功
        return userVo;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request)
    {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null || currentUser.getId() == null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request)
    {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserVO user = (UserVO) userObj;
        return user == null || !UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }

    @Override
    public boolean addWalletBalance(Long userId, Long addPoints)
    {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance + " + addPoints);
        return this.update(userLambdaUpdateWrapper);
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request)
    {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean updateUserInfo(Long id, HashMap<String, Object> newDict)
    {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", id);
        // 直接更新columnName字段， 放入newValue
        // 提取HashMap的Key和Value放进userUpdateWrapper里
        newDict.forEach(userUpdateWrapper::set);
        return userMapper.update(null, userUpdateWrapper) > 0;
    }

    /**
     * 验证账号密码是否正确
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户信息
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 2:23
     */
    @Override
    public User verifyUserPassword(String userAccount, String userPassword)
    {
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null)
        {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (!encryptionUtils.matches(userPassword, user.getUserPassword()))
        {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        return user;
    }

    /**
     * 校验账号密码是否符合规范
     *
     * @param userPassword  用户第壹次输入的密码
     * @param checkPassword 用户第贰次输入的密码
     * @param userAccount   用户账号
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/110 22:24
     */
    @Override
    public void validateAccountAndPassword(String userAccount, String userPassword, String checkPassword)
    {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
    }

    /**
     * 判断邮箱是否存在
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/11 16:57
     */
    @Override
    public boolean isEmailExist(String email)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.count(queryWrapper) > 0;
    }

    private boolean validatedCaptchaCode(String captchaId, String captchaCode)
    {
        // 1.2 校验验证码
        Map<Object, Object> result = redisOperatorService.getHash(RedisConstant.CAPTCHA_CODE_KEY + captchaId);
        if (result == null || result.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        String redisCode = result.get("code").toString().trim();
        String redisUuid = (String) result.get("uuid");
        redisOperatorService.delete(RedisConstant.CAPTCHA_CODE_KEY + redisUuid);
        // 验证码不区分大小写
        return redisCode.equalsIgnoreCase(captchaCode.trim());
    }

}




