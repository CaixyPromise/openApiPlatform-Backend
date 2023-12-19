package com.caixy.openapicommon.services;

import com.caixy.openapicommon.models.entity.InterfaceInfo;

/**
 * @Name: com.caixy.openapicommon.services.InnerInterfaceInfoService
 * @Description: 内部接口信息远程调用服务
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 18:40
 **/
public interface InnerInterfaceInfoService
{
    /**
     * 获取接口信息-获取接口id
     * @param  interfaceName 接口名称
     * @return 查询到的接口名称，否则为Null
     * @author CAIXYPROMISE
     * @since  2023/12/19 18:56
     * @updatedDate 2023/12/19 18:56
     * @version 1.0
     */
    Long getInterfaceId(String interfaceName);

}
