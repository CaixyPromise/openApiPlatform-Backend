package com.caixy.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.exception.BusinessException;
import com.caixy.project.model.entity.UserInterfaceInfo;
import com.caixy.project.service.UserInterfaceInfoService;
import com.caixy.project.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-12-20 22:18:43
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    /**
     * 更新接口调用次数统计
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2023/12/1 16:22
     */
    @Override
    public boolean updateUserInvokeCount(long interfaceId, long userId, int count)
    {
        // 1. 获取现有用户接口调用信息
        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .setSql("totalNum = totalNum + " + Math.abs(count))
                .setSql("leftNum = leftNum + " + count);

        boolean updated = this.update(updateWrapper);
        if (!updated) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return true;
    }
}




