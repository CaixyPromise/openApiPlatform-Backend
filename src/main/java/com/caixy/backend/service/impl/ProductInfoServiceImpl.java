package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.mapper.ProductInfoMapper;
import com.caixy.backend.model.entity.ProductInfo;
import com.caixy.backend.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author CAIXYPROMISE
 * @description 针对表【product_info(产品信息)】的数据库操作Service实现
 * @createDate 2024-03-11 02:06:28
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo>
        implements ProductInfoService
{

    @Override
    public void validProductInfo(ProductInfo productInfo, boolean add)
    {
        if (productInfo == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = productInfo.getName();
        String description = productInfo.getDescription();
        Long total = productInfo.getTotal();
        Date expirationTime = productInfo.getExpirationTime();
        String productType = productInfo.getProductType();
        Long addPoints = productInfo.getAddPoints();
        // 创建时，所有参数必须非空
        if (add)
        {
            if (StringUtils.isAnyBlank(name))
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (addPoints < 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "增加积分不能为负数");
        }
        if (total < 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "售卖金额不能为负数");
        }
    }
}




