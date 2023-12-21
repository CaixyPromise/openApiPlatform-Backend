package com.caixy.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.project.model.entity.UserInterfaceInfo;

/**
 * @author CAIXYPROMISE
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
 * @createDate 2023-12-20 22:18:43
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo>
{
    /**
     * 更新用户调用接口信息
     *
     * @author CAIXYPROMISE
     * @version a
     * @since 2023/12/1 16:14
     */
    boolean updateUserInvokeCount(long interfaceId, long userId, int count);
}
