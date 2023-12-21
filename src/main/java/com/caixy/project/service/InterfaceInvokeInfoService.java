package com.caixy.project.service;

import com.caixy.project.model.entity.InterfaceInvokeInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.project.model.vo.InterfaceInvokeCountVO;

/**
* @author CAIXYPROMISE
* @description 针对表【interface_invoke_info(调用接口数据统计表)】的数据库操作Service
* @createDate 2023-12-21 15:53:24
*/
public interface InterfaceInvokeInfoService extends IService<InterfaceInvokeInfo>
{
    InterfaceInvokeCountVO getInterfaceInvokeCount(Long interfaceId);
}
