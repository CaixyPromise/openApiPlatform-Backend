package com.caixy.backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.dto.user.UserLoginRequest;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 用户服务
 *
 * @author caixy
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param captchaId
     * @param captchaCode
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String captchaId, String captchaCode);

    /**
     * 用户登录
     *
     * @param userLoginRequest 密码body
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    boolean addWalletBalance(Long userId, Long addPoints);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    boolean updateUserInfo(Long id, HashMap<String, Object>newDict);

    User verifyUserPassword(String userAccount, String userPassword);

    // 校验账号密码是否符合规范
    void validateAccountAndPassword(String userAccount, String userPassword, String checkPassword);

    boolean isEmailExist(String email);
}
