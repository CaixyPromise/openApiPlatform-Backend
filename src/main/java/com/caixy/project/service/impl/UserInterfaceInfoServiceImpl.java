package com.caixy.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}




