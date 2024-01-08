package com.caixy.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoOffLineRequest;
import com.caixy.backend.model.dto.interfaceinfo.InterfaceInfoOnLineRequest;
import com.caixy.backend.common.BaseResponse;
import com.caixy.backend.model.entity.InterfaceInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;


/**
 * @author CAIXYPROMISE
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-12-10 21:43:05
 */
public interface InterfaceInfoService extends IService<InterfaceInfo>
{
    void validInterfaceInfo(InterfaceInfoAddRequest interfaceInfo);

    BaseResponse<?> InterfaceOnline(InterfaceInfoOnLineRequest info, HttpServletRequest request);

    InterfaceInfo getInterfaceInfo(Long interfaceId);

    BaseResponse<?> InterfaceOffline(InterfaceInfoOffLineRequest info, HttpServletRequest request);
    List<InterfaceInfo> getInterfaceInfosByIds(Set<Long> interfaceIds);
}
