package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.app.RefundApplyDTO;
import com.translation.entity.RefundRecord;
import com.translation.vo.admin.AdminRefundVO;

public interface RefundService extends IService<RefundRecord> {

    void applyRefund(Long userId, RefundApplyDTO dto);

    void approveRefund(Long refundId, Long adminId, String auditRemark);

    void rejectRefund(Long refundId, Long adminId, String auditRemark);

    Page<AdminRefundVO> getRefundPage(Integer status, int page, int size);
}