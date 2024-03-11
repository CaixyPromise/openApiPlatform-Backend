package com.caixy.backend.model.dto.product;

import com.caixy.backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 产品信息搜索文本请求
 *
 * @name: com.caixy.backend.model.dto.product.ProductInfoSearchTextRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:14
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoSearchTextRequest extends PageRequest implements Serializable
{
    private static final long serialVersionUID = -6337349622479990038L;

    private String searchText;
}
