package com.caixy.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.project.model.dto.interfaceinfo.InterfaceInfoOffLineRequest;
import com.caixy.project.model.dto.interfaceinfo.InterfaceInfoOnLineRequest;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.model.entity.InterfaceInfo;

import javax.servlet.http.HttpServletRequest;


/**
 * @author CAIXYPROMISE
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-12-10 21:43:05
 */
public interface InterfaceInfoService extends IService<InterfaceInfo>
{
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    BaseResponse<?> InterfaceOnline(InterfaceInfoOnLineRequest info, HttpServletRequest request);


    BaseResponse<?> InterfaceOffline(InterfaceInfoOffLineRequest info, HttpServletRequest request);

}
