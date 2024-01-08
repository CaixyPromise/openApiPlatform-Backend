package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.model.entity.InterfaceInvokeInfo;
import com.caixy.backend.model.vo.InterfaceInvokeCountVO;
import com.caixy.backend.service.InterfaceInvokeInfoService;
import com.caixy.backend.mapper.InterfaceInvokeInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【interface_invoke_info(调用接口数据统计表)】的数据库操作Service实现
* @createDate 2023-12-21 15:53:24
*/
@Service
public class InterfaceInvokeInfoServiceImpl extends ServiceImpl<InterfaceInvokeInfoMapper, InterfaceInvokeInfo>
    implements InterfaceInvokeInfoService{

    @Override
    public InterfaceInvokeCountVO getInterfaceInvokeCount(Long interfaceId)
    {
        QueryWrapper<InterfaceInvokeInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_id", interfaceId);
        InterfaceInvokeInfo interfaceInfo = baseMapper.selectOne(queryWrapper);
        if (interfaceInfo != null)
        {
            return new InterfaceInvokeCountVO(interfaceInfo.getTotalNum(),
                    interfaceInfo.getSuccessNum(), interfaceInfo.getFailNum());
        }
        return null;
    }
}




