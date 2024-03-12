package com.caixy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.caixy.backend.annotation.AuthCheck;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.DeleteRequest;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.constant.EmailConstant;
import com.caixy.backend.constant.RedisConstant;
import com.caixy.backend.constant.UserConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.exception.ThrowUtils;
import com.caixy.backend.mapper.UserMapper;
import com.caixy.backend.model.dto.user.*;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.vo.GetVoucherVO;
import com.caixy.backend.model.vo.SignatureVO;
import com.caixy.backend.model.vo.UpdateKeyVO;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.EncryptionUtils;
import com.caixy.backend.utils.RedisOperatorService;
import com.caixy.backend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * 用户接口
 *
 * @author caixy
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{
    @Value("${encryption.key}")
    private String SALT;

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private EncryptionUtils encryptionUtils;
    @Resource
    private RedisOperatorService redisOperatorService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest)
    {
        if (userRegisterRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String captchaCode = userRegisterRequest.getCaptcha();
        String captchaId = userRegisterRequest.getCaptchaId();
        if (StringUtils.isAnyBlank(
                userAccount,
                userPassword,
                checkPassword,
                captchaCode,
                captchaId))
        {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, captchaId, captchaCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request)
    {
        if (userLoginRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (StringUtils.isAnyBlank(userLoginRequest.getUserAccount(),
                userLoginRequest.getUserPassword(),
                userLoginRequest.getCaptcha(),
                userLoginRequest.getCaptchaId()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request)
    {
        if (request == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request)
    {
        UserVO user = userService.getLoginUser(request);
        return ResultUtils.success(user);
    }

    // endregion

    // region 用户信息增删改查

    /**
     * 创建用户(管理员权限)
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request)
    {
        if (userAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request)
    {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验token
        String token = userUpdateRequest.getToken();
        // redis校验token
        if (redisOperatorService.getString(RedisConstant.SIGNATURE_CODE_KEY + token) == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request)
    {
        User userQuery = new User();
        if (userQueryRequest != null)
        {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request)
    {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null)
        {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user ->
        {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
    // endregion

    // region 用户密钥操作

    /**
     * 获取key
     *
     * @author CAIXYPROMISE
     * @version
     * @since 2024/1/11 21:06
     */
    @PostMapping("/get_key")
    public BaseResponse<GetVoucherVO> getVoucherKey(@Valid @RequestBody UserGetLicenseRequest getLicenseRequest,
                                                    HttpServletRequest request)
    {
        // 获取登录信息
        UserVO userVO = userService.getLoginUser(request);
        // 验证密码
        User userInfo = userService.verifyUserPassword(userVO.getUserAccount(), getLicenseRequest.getPassword());
        // 检查时间戳是否超过五分钟
        if (System.currentTimeMillis() - Long.parseLong(getLicenseRequest.getTimestamp()) > 5 * 60 * 1000)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作失败");
        }
        // 检查随机数是否被使用?
        if (redisOperatorService.hasKey(RedisConstant.LICENSE_NONCE_KEY + getLicenseRequest.getNonce()))
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作失败");
        }
        // 存入随机数+时间戳
        redisOperatorService.setString(RedisConstant.LICENSE_NONCE_KEY + getLicenseRequest.getNonce(),
                getLicenseRequest.getTimestamp(),
                RedisConstant.NONCE_EXPIRE);
        GetVoucherVO voucherVO = new GetVoucherVO();
        voucherVO.setAccessKey(userInfo.getAccessKey());
        voucherVO.setSecretKey(userInfo.getSecretKey());
        return ResultUtils.success(voucherVO);
    }


    /**
     * 更新用户密钥的方法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 11:47
     */
    @GetMapping("/update_key")
    public BaseResponse<UpdateKeyVO> updateKey(
            HttpServletRequest request)
    {
        // 需要为登录状态
        UserVO curUser = userService.getLoginUser(request);
        User userQuery = userMapper.selectById(curUser.getId());
        // 3. 校验用户权限，只有用户自己和管理员才能调用
        if (!userQuery.getId().equals(curUser.getId()) &&
                !curUser.getUserRole().equals(UserConstant.DEFAULT_ROLE))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 4. 获取用户信息，并更新密钥
        HashMap<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("accessKey", encryptionUtils.makeUserKey(userQuery.getUserName()));
        updateInfo.put("secretKey", encryptionUtils.makeUserKey(userQuery.getUserName() + "." + SALT));
        userService.updateUserInfo(curUser.getId(), updateInfo);
        // 获取本次更新的内容, 把新更新的值返回
        UpdateKeyVO updateKeyVO = new UpdateKeyVO();
        updateKeyVO.setAccessKey(updateInfo.get("accessKey").toString());
        updateKeyVO.setSecretKey(updateInfo.get("secretKey").toString());
        return ResultUtils.success(updateKeyVO);
    }

    /**
     * 修改用户的accessKey和secretKey信息的签名认证
     * 只有管理员和自己能够调用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/13 19:43
     */
    @PostMapping("/signature")
    public BaseResponse<SignatureVO> getModifyLicense(@RequestBody UserGetLicenseRequest user, HttpServletRequest request)
    {
        // 1. 获取用户信息
        UserVO currentUser = userService.getLoginUser(request);
        // 2. 查询用户
        User userQuery = userMapper.selectById(currentUser.getId());
        // 3. 校验用户权限，只有用户自己和管理员才能调用
        if (!userQuery.getId().equals(currentUser.getId()) &&
                !currentUser.getUserRole().equals(UserConstant.DEFAULT_ROLE))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 2. 校验密码
        if (!encryptionUtils.matches(user.getPassword(), userQuery.getUserPassword()))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 生成签名
        String uuid = UUID.randomUUID().toString();
        // 4. 存入redis
        redisOperatorService.setString(RedisConstant.SIGNATURE_CODE_KEY + uuid, "1", RedisConstant.SIGNATURE_CODE_EXPIRE);
        SignatureVO signatureVO = new SignatureVO();
        signatureVO.setSignature(uuid);
        return ResultUtils.success(signatureVO);
    }
    // endregion

    // region 修改用户密保：修改密码/设置邮箱/解绑
    @PostMapping("/modify/password")
    public BaseResponse<Boolean> modifyPassword(@RequestBody ModifyPasswordRequest modifyPasswordRequest,
                                                HttpServletRequest request)
    {
        if (modifyPasswordRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("received request: {}", modifyPasswordRequest.toString());
        // 1. 参数校验
        String userOldPassword = modifyPasswordRequest.getOldPassword();
        String checkPassword = modifyPasswordRequest.getNewPassword();
        String userNewPassword = modifyPasswordRequest.getNewPassword();
        String signature = modifyPasswordRequest.getSignature();
        ThrowUtils.throwIf(StringUtils.isAnyBlank(signature, userOldPassword, checkPassword, userNewPassword),
                ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(!checkPassword.equals(userNewPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        // 2. 获取当前登录的用户信息，并且需要登录
        UserVO requestVO = userService.getLoginUser(request);
        // 3. 获取数据库内的用户信息
        User dbUser = userMapper.selectById(requestVO.getId());
        // 4. 检查是否绑定邮箱
        ThrowUtils.throwIf(dbUser.getEmail() == null, ErrorCode.SYSTEM_ERROR, "用户未绑定邮箱, 请先绑定再操作");
        String userAccount = dbUser.getUserAccount();
        // 5. 判断原密码是否正确,
        userService.verifyUserPassword(userAccount, userOldPassword);
        // 5.1 判断原密码和新密码是否一致
        ThrowUtils.throwIf(encryptionUtils.matches(userNewPassword, dbUser.getUserPassword()), ErrorCode.OPERATION_ERROR, "原密码不能与新密码相同");
        // 6. 判断新密码是否符合规范
        userService.validateAccountAndPassword(userAccount, userNewPassword, checkPassword);
        ThrowUtils.throwIf(!RegexUtils.validateUsernameAndPassword(userAccount, userNewPassword), ErrorCode.OPERATION_ERROR, "密码不符合规范");
        // 7. 检查验证码是否正确
        String redisKey = EmailConstant.EMAIL_MODIFY_PASSWORD_CACHE_KEY + dbUser.getEmail();
        validateEmailRedisKey(redisKey, modifyPasswordRequest.getEmailCode(), modifyPasswordRequest.getSignature());
        // 更新账号密码
        // 密码加密
        String encryptPassword = encryptionUtils.encodePassword(userNewPassword);
        dbUser.setUserPassword(encryptPassword);
        userMapper.updateById(dbUser);
        // 更改密码后要重新登录
        userService.userLogout(request);
        return ResultUtils.success(true);
    }

    /**
     * 修改邮箱
     * 此处是发起校验用户修改邮箱是否合法，发送验证码给之前的邮箱
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/10 22:33
     */
    @PostMapping("/modify/email")
    public BaseResponse<Boolean> modifyUserEmail(@Valid @RequestBody ModifyUserEmailRequest modifyUserEmailRequest,
                                                 HttpServletRequest request)
    {

        if (modifyUserEmailRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 获取当前用户
        UserVO curUser = userService.getLoginUser(request);
        // 检查签名是否存在且合规
        String redisKey = EmailConstant.EMAIL_NEW_CAPTCHA_CACHE_KEY + modifyUserEmailRequest.getEmail();
        validateEmailRedisKey(redisKey, modifyUserEmailRequest.getCode(), modifyUserEmailRequest.getSignature());
        // 更新邮箱
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", curUser.getId());
        updateWrapper.set("email", modifyUserEmailRequest.getEmail());
        if (userService.update(updateWrapper))
        {
            // 删除key
            redisOperatorService.delete(redisKey);
            return ResultUtils.success(true);
        }
        return ResultUtils.success(false);
    }

    /**
     * 验证redis里的邮箱验证码是否正确
     *
     * @param redisKey redis的操作key
     * @param code     用户输入的验证码
     * @param sign     redis里存储的验证码
     * @author CAIXYPROMISE
     * @version a
     * @since 2024/1/2 18:58
     */
    private void validateEmailRedisKey(String redisKey, String code, String sign)
    {
        Map<Object, Object> redisInfo = redisOperatorService.getHash(redisKey);
        if (redisInfo == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不存在或请求已过期");
        }
        // 检查验证码是否正确
        // key: 邮箱 -> {
        //     签名 -> 验证码
        // }
        String signCode = (String) redisInfo.get(sign);
        if (signCode == null || !signCode.equals(code))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不存在或已过期");
        }
    }
    // endregion
}
