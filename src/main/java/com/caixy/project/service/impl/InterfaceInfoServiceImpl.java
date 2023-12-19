package com.caixy.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.project.mapper.InterfaceInfoMapper;
import com.caixy.project.model.dto.interfaceinfo.InterfaceInfoOffLineRequest;
import com.caixy.project.model.dto.interfaceinfo.InterfaceInfoOnLineRequest;
import com.caixy.project.service.InterfaceInfoService;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.model.entity.InterfaceInfo;
import com.caixy.project.model.entity.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.caixy.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author CAIXYPROMISE
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-12-10 21:43:05
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService
{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add)
    {

    }

    /**
     * @param info    接口信息
     * @param request 请求信息
     * @author CAIXYPROMISE
     * @description 判断接口是否在线, 如果不在线就给它在线
     * @date 2023-12-15
     */
    @Override
    public BaseResponse<?> InterfaceOnline(InterfaceInfoOnLineRequest info, HttpServletRequest request)
    {
        // 1. 获取当前用户
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 2. 查询接口信息
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", info.getId());
        InterfaceInfo interfaceInfo = baseMapper.selectOne(queryWrapper);
        // 3. 检查接口状态
        // 如果接口不存在
        if (interfaceInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 接口在线状态就不用处理
        if (interfaceInfo.getStatus() == 1)
        {
            return ResultUtils.success("接口已上线");
        }
        // 4. 设置接口为在线状态
        interfaceInfo.setStatus(1);
        // 只有所有权用户或者管理员才能操作这个接口
        if (currentUser.getUserRole().equals("admin") ||
                interfaceInfo.getUserId().equals(currentUser.getId()))
        {
            baseMapper.updateById(interfaceInfo);
            return ResultUtils.success("接口已下线");
        }
        else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);

        }
    }
    /**
     *
     * @param info 需要下线的接口id
     * @param request 请求信息
     * @author CAIXYPROMISE
     * @date 2023-12-15
     * @return handle-result
    * */
    @Override
    public BaseResponse<?> InterfaceOffline(InterfaceInfoOffLineRequest info, HttpServletRequest request)
    {
        // 1. 获取当前用户
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 2. 查询接口信息
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", info.getId());
        InterfaceInfo interfaceInfo = baseMapper.selectOne(queryWrapper);
        // 3. 检查接口状态
        // 如果接口不存在
        if (interfaceInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 接口下线状态就不用处理
        if (interfaceInfo.getStatus() == 0)
        {
            return ResultUtils.success("接口已上线");
        }
        // 4. 设置接口为下线状态
        interfaceInfo.setStatus(0);
        // 只有所有权用户或者管理员才能操作这个接口
        System.out.println("currentUser" + currentUser);
        System.out.println("interfaceInfo" + interfaceInfo);
        if (currentUser.getUserRole().equals("admin") ||
                interfaceInfo.getUserId().equals(currentUser.getId()))
        {
            baseMapper.updateById(interfaceInfo);
            return ResultUtils.success("接口已下线");
        }
        else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);

        }
    }
}




