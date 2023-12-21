package com.caixy.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caixy.openapicommon.services.InnerInterfaceInfoService;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.mapper.InterfaceInfoMapper;
import com.caixy.project.mapper.InterfaceInvokeInfoMapper;
import com.caixy.project.model.entity.InterfaceInfo;
import com.caixy.project.model.entity.InterfaceInvokeInfo;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * 内部接口信息远程调用服务实现
 *
 * @name: com.yupi.project.service.impl.inner.InnerInterfaceInfoServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2023-12-19 21:28
 **/
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService
{
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public Long getInterfaceId(String interfaceName)
    {
        if (StringUtils.isBlank(interfaceName))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", interfaceName);
        InterfaceInfo result = interfaceInfoMapper.selectOne(queryWrapper);
        if (result != null)
        {
            return result.getId();
        }
        return -1L;
    }
}
