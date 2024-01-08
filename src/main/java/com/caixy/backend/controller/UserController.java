package com.caixy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.caixy.backend.annotation.AuthCheck;
import com.caixy.backend.constant.RedisConstant;
import com.caixy.backend.constant.UserConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.mapper.UserMapper;
import com.caixy.backend.model.dto.user.*;
import com.caixy.backend.model.vo.SignatureVO;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.DeleteRequest;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.EncryptionUtils;
import com.caixy.backend.utils.RedisOperatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author caixy
 */
@RestController
@RequestMapping("/user")
public class UserController
{

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
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword))
        {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
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

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
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
    @GetMapping("/update_key")
    public BaseResponse<Boolean> updateKey(@RequestParam String token,
            @RequestParam String name, HttpServletRequest request)
    {
        if (token == null || name == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO curUser = getCurrentUser(request);


        return ResultUtils.success(updateUserInfo(token,
                curUser, name));
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

    /**
     * 修改用户的accessKey和secretKey信息的签名认证
     * 只有管理员和自己能够调用
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/13 19:43
     */
    @PostMapping("/signature")
    public BaseResponse<SignatureVO> getModifyLicense(UserGetLicenseRequest user, HttpServletRequest request)
    {
        // 1. 获取用户信息
        UserVO currentUser = getCurrentUser(request);
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

    private UserVO getCurrentUser(HttpServletRequest request)
    {
        UserVO currentUser = userService.getLoginUser(request);
        if (currentUser == null)
        {
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    private boolean updateUserInfo(String token, UserVO curUser, String columnName)
    {
        // 校验token
        // redis校验token
        if (redisOperatorService.getString(RedisConstant.SIGNATURE_CODE_KEY + token) == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 校验完就删掉
        // 防止重复提交
        redisOperatorService.delete(RedisConstant.SIGNATURE_CODE_KEY + token);
        return userService.updateUserInfo(curUser.getId(),
                columnName,
                encryptionUtils.makeUserKey(curUser.getUserName()));
    }

}
