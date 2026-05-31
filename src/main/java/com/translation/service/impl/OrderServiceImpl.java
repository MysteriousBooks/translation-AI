package com.translation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.constant.CommonConstant;
import com.translation.common.enums.OrderStatus;
import com.translation.common.enums.PayType;
import com.translation.common.enums.ResultCode;
import com.translation.common.enums.WalletRecordType;
import com.translation.common.exception.BusinessException;
import com.translation.dto.app.RechargeDTO;
import com.translation.entity.Order;
import com.translation.entity.User;
import com.translation.mapper.OrderMapper;
import com.translation.mapper.UserMapper;
import com.translation.service.OrderService;
import com.translation.service.UserService;
import com.translation.service.pay.PayService;
import com.translation.vo.admin.AdminOrderVO;
import com.translation.vo.app.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Qualifier("alipayService")
    private final PayService alipayService;

    @Qualifier("wechatPayService")
    private final PayService wechatPayService;

    @Override
    @Transactional
    public OrderVO createRechargeOrder(Long userId, RechargeDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Order order = new Order();
        order.setOrderNo(CommonConstant.ORDER_NO_PREFIX + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setPayType(dto.getPayType());
        order.setAmount(dto.getAmount());
        order.setStatus(OrderStatus.PENDING.getCode());
        orderMapper.insert(order);

        PayService payService;
        String payData;
        if (dto.getPayType() == PayType.ALIPAY.getCode()) {
            payService = alipayService;
        } else if (dto.getPayType() == PayType.WECHAT.getCode()) {
            payService = wechatPayService;
        } else {
            throw new BusinessException("不支持的支付类型");
        }

        payData = payService.createOrder(
                order.getOrderNo(),
                order.getAmount(),
                "AI翻译服务充值",
                null,
                null
        );

        OrderVO vo = toOrderVO(order);
        vo.setPayData(payData);
        return vo;
    }

    @Override
    @Transactional
    public void handleAlipayCallback(Map<String, String> params) {
        if (!alipayService.verifyCallback(params)) {
            log.error("支付宝回调签名验证失败");
            return;
        }

        String orderNo = params.get("out_trade_no");
        String transactionId = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            log.info("支付宝回调非成功状态: {}", tradeStatus);
            return;
        }

        Order order = lambdaQuery().eq(Order::getOrderNo, orderNo).one();
        if (order == null) {
            log.error("支付宝回调订单不存在: {}", orderNo);
            return;
        }
        if (order.getStatus() != OrderStatus.PENDING.getCode()) {
            return;
        }

        order.setStatus(OrderStatus.PAID.getCode());
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(transactionId);
        orderMapper.updateById(order);

        userService.updateUserBalance(order.getUserId(), order.getAmount(),
                "充值", order.getOrderNo(), WalletRecordType.RECHARGE.getCode());
    }

    @Override
    @Transactional
    public void handleWechatCallback(Map<String, String> params) {
        if (!wechatPayService.verifyCallback(params)) {
            log.error("微信回调签名验证失败");
            return;
        }

        String body = params.get("body");
        if (StrUtil.isBlank(body)) {
            log.error("微信回调body为空");
            return;
        }

        try {
            cn.hutool.json.JSONObject bodyJson = cn.hutool.json.JSONUtil.parseObj(body);
            if (!bodyJson.containsKey("resource")) {
                log.error("微信回调数据缺少resource字段");
                return;
            }
            cn.hutool.json.JSONObject resource = bodyJson.getJSONObject("resource");
            String ciphertext = resource.getStr("ciphertext");
            String nonce = resource.getStr("nonce");
            String associatedData = resource.getStr("associated_data", "");

            PayService wechatService = wechatPayService;
            String decrypted;
            if (wechatService instanceof com.translation.service.pay.WechatPayServiceImpl) {
                decrypted = ((com.translation.service.pay.WechatPayServiceImpl) wechatService)
                        .decryptResource(ciphertext, nonce, associatedData);
            } else {
                log.error("微信支付服务类型异常");
                return;
            }

            cn.hutool.json.JSONObject decryptedJson = cn.hutool.json.JSONUtil.parseObj(decrypted);
            String orderNo = decryptedJson.getStr("out_trade_no");
            String transactionId = decryptedJson.getStr("transaction_id");
            String tradeState = decryptedJson.getStr("trade_state");

            if (!"SUCCESS".equals(tradeState)) {
                log.info("微信回调非成功状态: {}", tradeState);
                return;
            }

            Order order = lambdaQuery().eq(Order::getOrderNo, orderNo).one();
            if (order == null) {
                log.error("微信回调订单不存在: {}", orderNo);
                return;
            }
            if (order.getStatus() != OrderStatus.PENDING.getCode()) {
                return;
            }

            order.setStatus(OrderStatus.PAID.getCode());
            order.setPayTime(LocalDateTime.now());
            order.setTransactionId(transactionId);
            orderMapper.updateById(order);

            userService.updateUserBalance(order.getUserId(), order.getAmount(),
                    "充值", order.getOrderNo(), WalletRecordType.RECHARGE.getCode());
        } catch (Exception e) {
            log.error("微信回调处理异常", e);
        }
    }

    @Override
    public OrderVO getOrderStatus(Long userId, String orderNo) {
        Order order = lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return toOrderVO(order);
    }

    @Override
    public Page<OrderVO> getOrderPage(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        Page<Order> result = lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .page(pageParam);

        Page<OrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toOrderVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public Page<AdminOrderVO> getAdminOrderPage(String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> pageParam = new Page<>(page, size);
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);

        Page<AdminOrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toAdminOrderVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public AdminOrderVO getAdminOrderDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return toAdminOrderVO(order);
    }

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);
        return vo;
    }

    private AdminOrderVO toAdminOrderVO(Order order) {
        AdminOrderVO vo = new AdminOrderVO();
        BeanUtil.copyProperties(order, vo);

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            vo.setUserEmail(user.getEmail());
            vo.setUserNickname(user.getNickname());
        }
        return vo;
    }
}