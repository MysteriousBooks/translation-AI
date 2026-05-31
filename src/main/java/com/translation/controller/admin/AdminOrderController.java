package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.service.OrderService;
import com.translation.vo.admin.AdminOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台-订单管理")
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @ApiOperation("订单列表")
    @GetMapping("/list")
    public Result<PageResult<AdminOrderVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminOrderVO> result = orderService.getAdminOrderPage(keyword, status, page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<AdminOrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getAdminOrderDetail(id));
    }
}