package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.common.ResultUtils;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.exception.ThrowUtils;
import com.caixy.backend.mapper.InterfaceInfoMapper;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoOffLineRequest;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoOnLineRequest;
import com.caixy.backend.model.entity.InterfaceInfo;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.service.InterfaceInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static com.caixy.backend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author CAIXYPROMISE
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-12-10 21:43:05
 */
@Service
@Slf4j
@AllArgsConstructor
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService
{
    /**
     * 根据接口id获取接口信息
     *
     * @param interfaceId 接口id
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/26 16:35
     */
    @Override
    public InterfaceInfo getInterfaceInfo(Long interfaceId)
    {
        // 获取接口信息
        InterfaceInfo interfaceInfo = baseMapper.selectById(interfaceId);
        if (interfaceInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return interfaceInfo;
    }

    /**
     * 校验接口信息是否合法
     *
     * @param interfaceInfo 接口信息
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/1/5 14:48
     */
    @Override
    public void validInterfaceInfo(InterfaceInfoAddRequest interfaceInfo)
    {
        log.info("interfaceInfo: {}", interfaceInfo);
        // 名称校验
        ThrowUtils.throwIf(interfaceInfo.getName() == null || interfaceInfo.getName().isEmpty(),
                ErrorCode.PARAMS_ERROR, "接口名称不能为空");
        // URL校验
        ThrowUtils.throwIf(interfaceInfo.getUrl() == null || interfaceInfo.getUrl().isEmpty(),
                ErrorCode.PARAMS_ERROR, "接口地址不能为空");
        // 正则表达式匹配URL
        ThrowUtils.throwIf(!isValidUrl(interfaceInfo.getUrl()),
                ErrorCode.PARAMS_ERROR, "无效的URL格式");

        // 请求方法校验
        String method = interfaceInfo.getMethod();
        ThrowUtils.throwIf(method == null || !(method.equalsIgnoreCase("GET") ||
                        method.equalsIgnoreCase("POST") ||
                        method.equalsIgnoreCase("PUT") ||
                        method.equalsIgnoreCase("DELETE")),
                ErrorCode.PARAMS_ERROR, "不支持的请求方法");

        // 请求载荷、请求头和响应头的校验
        validatePayload(interfaceInfo.getRequestPayload(), "请求载荷");
        validatePayload(interfaceInfo.getRequestHeader(), "请求头");
        validatePayload(interfaceInfo.getResponseHeader(), "响应头");
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
        else
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);

        }
    }

    /**
     * @param info    需要下线的接口id
     * @param request 请求信息
     * @return handle-result
     * @author CAIXYPROMISE
     * @date 2023-12-15
     */
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
        else
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);

        }
    }

    @Override
    public List<InterfaceInfo> getInterfaceInfosByIds(Set<Long> interfaceIds)
    {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceIds);
        return this.list(queryWrapper);
    }


    private boolean isValidUrl(String url)
    {
        String urlRegex = "^(http[s]?://)?([\\w.-]+(?:\\.[\\w.-]+)+|localhost)?[\\w\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*$";
        return url.matches(urlRegex);
    }


    private void validatePayload(List<?> payload, String payloadType)
    {
        if (payload != null)
        {
            payload.forEach(item -> {
                if (item instanceof String)
                {
                    String payloadStr = (String) item;
                    ThrowUtils.throwIf(Character.isDigit(payloadStr.charAt(0)),
                            ErrorCode.PARAMS_ERROR, payloadType + "参数格式错误");
                }
            });
        }
//        if (payload != null && !payload.isEmpty())
//        {
//            ThrowUtils.throwIf(Character.isDigit(payload.charAt(0)),
//                    ErrorCode.PARAMS_ERROR, payloadType + "不能以数字开头");
//        }
    }
}




