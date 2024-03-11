package com.caixy.backend.service;

import com.caixy.backend.model.entity.ProductOrder;
import com.caixy.backend.model.vo.ProductOrderVo;
import com.caixy.backend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Name: com.caixy.backend.service.OrderService
 * @Description: 订单服务
 * @Author: CAIXYPROMISE
 * @Date: 2024-03-11 02:50
 **/
public interface OrderService
{
    /**
     * 处理订单通知
     *
     * @param notifyData 通知数据
     * @param request    要求
     * @return {@link String}
     */
    String doOrderNotify(String notifyData, HttpServletRequest request);

    /**
     * 按付费类型获取产品订单服务
     *
     * @param payType 付款类型
     * @return {@link ProductOrderService}
     */
    ProductOrderService getProductOrderServiceByPayType(String payType);

    /**
     * 按付款类型创建订单
     *
     * @param productId 产品id
     * @param payType   付款类型
     * @param loginUser 登录用户
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo createOrderByPayType(Long productId, String payType, UserVO loginUser);

    /**
     * 按时间获得未支付订单
     *
     * @param minutes 分钟
     * @param remove  是否是删除
     * @param payType 付款类型
     * @return {@link List}<{@link ProductOrder}>
     */
    List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType);
}
