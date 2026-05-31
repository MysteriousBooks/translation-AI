package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.admin.RefundAuditDTO;
import com.translation.service.RefundService;
import com.translation.vo.admin.AdminRefundVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台-退款管理")
@RestController
@RequestMapping("/api/admin/refund")
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundService refundService;

    @ApiOperation("退款列表")
    @GetMapping("/list")
    public Result<PageResult<AdminRefundVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminRefundVO> result = refundService.getRefundPage(status, page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("同意退款")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @Validated @RequestBody RefundAuditDTO dto) {
        refundService.approveRefund(id, UserContext.getRequiredAdminId(), dto.getAuditRemark());
        return Result.success();
    }

    @ApiOperation("拒绝退款")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @Validated @RequestBody RefundAuditDTO dto) {
        refundService.rejectRefund(id, UserContext.getRequiredAdminId(), dto.getAuditRemark());
        return Result.success();
    }
}