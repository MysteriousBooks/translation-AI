package com.translation.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.constant.CommonConstant;
import com.translation.common.enums.OrderStatus;
import com.translation.common.enums.RefundStatus;
import com.translation.common.enums.ResultCode;
import com.translation.common.enums.WalletRecordType;
import com.translation.common.exception.BusinessException;
import com.translation.dto.app.RefundApplyDTO;
import com.translation.entity.Order;
import com.translation.entity.RefundRecord;
import com.translation.entity.User;
import com.translation.mapper.OrderMapper;
import com.translation.mapper.RefundRecordMapper;
import com.translation.mapper.UserMapper;
import com.translation.service.RefundService;
import com.translation.service.UserService;
import com.translation.vo.admin.AdminRefundVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundService {

    private final RefundRecordMapper refundRecordMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    @Transactional
    public void applyRefund(Long userId, RefundApplyDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        /* 检查是否已申请退款 */
        long count = lambdaQuery()
                .eq(RefundRecord::getOrderId, dto.getOrderId())
                .ne(RefundRecord::getStatus, RefundStatus.REJECTED.getCode())
                .count();
        if (count > 0) {
            throw new BusinessException(ResultCode.REFUND_APPLY_EXISTS);
        }

        RefundRecord record = new RefundRecord();
        record.setOrderId(dto.getOrderId());
        record.setUserId(userId);
        record.setRefundNo(CommonConstant.REFUND_NO_PREFIX + IdUtil.getSnowflakeNextIdStr());
        record.setAmount(order.getAmount());
        record.setReason(dto.getReason());
        record.setStatus(RefundStatus.PENDING.getCode());
        refundRecordMapper.insert(record);

        /* 更新订单状态为退款中 */
        order.setStatus(OrderStatus.REFUNDED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void approveRefund(Long refundId, Long adminId, String auditRemark) {
        RefundRecord record = refundRecordMapper.selectById(refundId);
        if (record == null) {
            throw new BusinessException(ResultCode.REFUND_NOT_FOUND);
        }
        if (record.getStatus() != RefundStatus.PENDING.getCode()) {
            throw new BusinessException("退款记录状态不允许审核");
        }

        record.setStatus(RefundStatus.APPROVED.getCode());
        record.setAdminId(adminId);
        record.setAuditRemark(auditRemark);
        refundRecordMapper.updateById(record);

        /* 执行退款 - 扣除用户余额 */
        userService.updateUserBalance(record.getUserId(), record.getAmount(),
                "退款", record.getRefundNo(), WalletRecordType.REFUND.getCode());

        /* 更新退款状态为已完成 */
        record.setStatus(RefundStatus.COMPLETED.getCode());
        refundRecordMapper.updateById(record);

        Order order = orderMapper.selectById(record.getOrderId());
        if (order != null) {
            order.setStatus(OrderStatus.REFUNDED.getCode());
            orderMapper.updateById(order);
        }
    }

    @Override
    @Transactional
    public void rejectRefund(Long refundId, Long adminId, String auditRemark) {
        RefundRecord record = refundRecordMapper.selectById(refundId);
        if (record == null) {
            throw new BusinessException(ResultCode.REFUND_NOT_FOUND);
        }
        if (record.getStatus() != RefundStatus.PENDING.getCode()) {
            throw new BusinessException("退款记录状态不允许审核");
        }

        record.setStatus(RefundStatus.REJECTED.getCode());
        record.setAdminId(adminId);
        record.setAuditRemark(auditRemark);
        refundRecordMapper.updateById(record);

        /* 恢复订单状态为已支付 */
        Order order = orderMapper.selectById(record.getOrderId());
        if (order != null && order.getStatus() == OrderStatus.REFUNDED.getCode()) {
            order.setStatus(OrderStatus.PAID.getCode());
            orderMapper.updateById(order);
        }
    }

    @Override
    public Page<AdminRefundVO> getRefundPage(Integer status, int page, int size) {
        LambdaQueryWrapper<RefundRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(RefundRecord::getStatus, status);
        }
        wrapper.orderByDesc(RefundRecord::getCreateTime);

        Page<RefundRecord> pageParam = new Page<>(page, size);
        Page<RefundRecord> result = refundRecordMapper.selectPage(pageParam, wrapper);

        Page<AdminRefundVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    private AdminRefundVO toVO(RefundRecord record) {
        AdminRefundVO vo = new AdminRefundVO();
        vo.setId(record.getId());
        vo.setRefundNo(record.getRefundNo());
        vo.setOrderId(record.getOrderId());
        vo.setUserId(record.getUserId());

        Order order = orderMapper.selectById(record.getOrderId());
        if (order != null) {
            vo.setOrderNo(order.getOrderNo());
        }

        User user = userMapper.selectById(record.getUserId());
        if (user != null) {
            vo.setUserEmail(user.getEmail());
        }

        vo.setAmount(record.getAmount());
        vo.setReason(record.getReason());
        vo.setStatus(record.getStatus());
        vo.setAdminId(record.getAdminId());
        vo.setAuditRemark(record.getAuditRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
        return vo;
    }
}