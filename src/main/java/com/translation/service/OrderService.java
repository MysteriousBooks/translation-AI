package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.app.RechargeDTO;
import com.translation.entity.Order;
import com.translation.vo.admin.AdminOrderVO;
import com.translation.vo.app.OrderVO;

public interface OrderService extends IService<Order> {

    OrderVO createRechargeOrder(Long userId, RechargeDTO dto);

    void handleAlipayCallback(java.util.Map<String, String> params);

    void handleWechatCallback(java.util.Map<String, String> params);

    OrderVO getOrderStatus(Long userId, String orderNo);

    Page<OrderVO> getOrderPage(Long userId, int page, int size);

    Page<AdminOrderVO> getAdminOrderPage(String keyword, Integer status, int page, int size);

    AdminOrderVO getAdminOrderDetail(Long id);
}