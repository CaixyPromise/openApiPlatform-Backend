package com.caixy.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.entity.ProductInfo;

/**
 * @author CAIXYPROMISE
 * @description 针对表【product_info(产品信息)】的数据库操作Service
 * @createDate 2024-03-11 02:06:28
 */
public interface ProductInfoService extends IService<ProductInfo>
{
    /**
     * 有效产品信息
     * 校验
     *
     * @param add         是否为创建校验
     * @param productInfo 产品信息
     */
    void validProductInfo(ProductInfo productInfo, boolean add);
}
