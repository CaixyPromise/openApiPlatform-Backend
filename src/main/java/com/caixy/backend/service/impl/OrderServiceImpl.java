package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.model.entity.ProductInfo;
import com.caixy.backend.model.entity.ProductOrder;
import com.caixy.backend.model.entity.RechargeActivity;
import com.caixy.backend.model.enums.PayTypeStatusEnum;
import com.caixy.backend.model.enums.PaymentStatusEnum;
import com.caixy.backend.model.enums.ProductTypeStatusEnum;
import com.caixy.backend.model.vo.ProductOrderVo;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.OrderService;
import com.caixy.backend.service.ProductOrderService;
import com.caixy.backend.service.RechargeActivityService;
import com.caixy.backend.utils.RedisOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 订单服务实现类
 *
 * @name: com.caixy.backend.service.impl.OrderServiceImpl
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:52
 **/
@Slf4j
@Service
public class OrderServiceImpl implements OrderService
{

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private List<ProductOrderService> productOrderServices;

    @Resource
    private RechargeActivityService rechargeActivityService;

    @Resource
    private ProductInfoServiceImpl productInfoService;

    @Resource
    private RedisOperatorService redisOperatorService;

    /**
     * 按付费类型获取产品订单服务
     *
     * @param payType 付款类型
     * @return {@link ProductOrderService}
     */
    @Override
    public ProductOrderService getProductOrderServiceByPayType(String payType)
    {
        return productOrderServices.stream()
                .filter(s -> {
                    Qualifier qualifierAnnotation = s.getClass().getAnnotation(Qualifier.class);
                    if (qualifierAnnotation != null)
                    {
                        log.info("qualifierAnnotation.value: {}", qualifierAnnotation.value());

                    }
                    return qualifierAnnotation != null && qualifierAnnotation.value().equals(payType);
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductOrderVo createOrderByPayType(Long productId, String payType, UserVO loginUser)
    {
        // 按付费类型获取产品订单服务Bean
        ProductOrderService productOrderService = getProductOrderServiceByPayType(payType);
        String redissonLock = "getOrder:";

        boolean distributedLock =
                redisOperatorService.tryGetDistributedLock(redissonLock, loginUser.getUserAccount().intern(), 5L);
        if (!distributedLock)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单操作失败，请稍后再试");
        }
        else
        {
            ProductOrderVo getProductOrderVo = productOrderService.getProductOrder(productId, loginUser, payType);
            if (getProductOrderVo != null)
            {
                return getProductOrderVo;
            }
            redissonLock = ("createOrder_" + loginUser.getUserAccount()).intern();
            boolean createOrderLock =
                    redisOperatorService.tryGetDistributedLock(redissonLock, loginUser.getUserAccount().intern(), 5L);
            if (!createOrderLock)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败，请稍后再试");
            }
            else
            {
                // 检查是否购买充值活动
                checkBuyRechargeActivity(loginUser.getId(), productId);
                // 保存订单,返回vo信息
                return productOrderService.saveProductOrder(productId, loginUser);
            }
        }
    }

    /**
     * 检查购买充值活动
     *
     * @param userId    用户id
     * @param productId 产品订单id
     */
    private void checkBuyRechargeActivity(Long userId, Long productId)
    {
        ProductInfo productInfo = productInfoService.getById(productId);
        log.info("productInfo.id:{}", productId);

        if (productInfo == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "产品不存在");
        }
        if (productInfo.getProductType().equals(ProductTypeStatusEnum.RECHARGE_ACTIVITY.getValue()))
        {
            LambdaQueryWrapper<ProductOrder> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderLambdaQueryWrapper.eq(ProductOrder::getUserId, userId);
            orderLambdaQueryWrapper.eq(ProductOrder::getProductId, productId);
            orderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
            orderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.SUCCESS.getValue());

            long orderCount = productOrderService.count(orderLambdaQueryWrapper);
            if (orderCount > 0)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该商品只能购买一次，请查看是否已经创建了该订单，或者挑选其他商品吧！");
            }
            LambdaQueryWrapper<RechargeActivity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            activityLambdaQueryWrapper.eq(RechargeActivity::getUserId, userId);
            activityLambdaQueryWrapper.eq(RechargeActivity::getProductId, productId);
            long count = rechargeActivityService.count(activityLambdaQueryWrapper);
            if (count > 0)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该商品只能购买一次，请查看是否已经创建了该订单，或者挑选其他商品吧！！");
            }
        }
    }

    /**
     * 查找超过minutes分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @return {@link List}<{@link ProductOrder}>
     */
    @Override
    public List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType)
    {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
        if (StringUtils.isNotBlank(payType))
        {
            productOrderLambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        }
        // 删除
        if (remove)
        {
            productOrderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.CLOSED.getValue());
        }
        productOrderLambdaQueryWrapper.and(p -> p.le(ProductOrder::getCreateTime, instant));
        return productOrderService.list(productOrderLambdaQueryWrapper);
    }

    /**
     * 做订单通知
     * 支票支付类型
     *
     * @param notifyData 通知数据
     * @param request    要求
     * @return {@link String}
     */
    @Override
    public String doOrderNotify(String notifyData, HttpServletRequest request)
    {
        String payType;
        if (notifyData.startsWith("gmt_create=") && notifyData.contains("gmt_create") && notifyData.contains("sign_type") && notifyData.contains("notify_type"))
        {
            payType = PayTypeStatusEnum.ALIPAY.getValue();
        }
        else
        {
            payType = PayTypeStatusEnum.WECHAT.getValue();
        }
        return this.getProductOrderServiceByPayType(payType).doPaymentNotify(notifyData, request);
    }
}
