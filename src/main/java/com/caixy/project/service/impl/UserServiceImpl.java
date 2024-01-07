package com.caixy.project.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.constant.UserConstant;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.model.dto.user.UserLoginRequest;
import com.caixy.project.model.vo.UserVO;
import com.caixy.project.utils.EncryptionUtils;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.mapper.UserMapper;
import com.caixy.project.model.entity.User;
import com.caixy.project.service.UserService;
import com.caixy.project.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public long userRegister(String userAccount, String userPassword, String checkPassword)
    {
        // 1. 校验
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
            String secretKey = encryptionUtils.makeUserKey(userPassword);
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
        Map<Object, Object> result = redisOperatorService.getHash(RedisConstant.CAPTCHA_CODE_KEY, captchaId);
        if (result == null || result.isEmpty())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        String redisCode = result.get("code").toString().trim();
        String redisUuid = (String) result.get("uuid");
        redisOperatorService.delete(RedisConstant.CAPTCHA_CODE_KEY + redisUuid);
        // 验证码不区分大小写
        if (!redisCode.equalsIgnoreCase(captcha.trim()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        // 2. 加密

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
        // 3. 记录用户的登录态
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(user, userVo);
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
        User user = (User) userObj;
        return user == null || !UserConstant.ADMIN_ROLE.equals(user.getUserRole());
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
    public boolean updateUserInfo(Long id, String columnName, String newValue)
    {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", id);
        // 直接更新columnName字段， 放入newValue
        userUpdateWrapper.set(columnName, newValue);
        return userMapper.update(null, userUpdateWrapper) > 0;
    }

}




