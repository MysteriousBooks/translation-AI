package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.entity.WalletRecord;
import com.translation.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "后台-消耗流水")
@RestController
@RequestMapping("/api/admin/consume")
@RequiredArgsConstructor
public class AdminConsumeController {

    private final WalletService walletService;

    @ApiOperation("消耗流水列表")
    @GetMapping("/list")
    public Result<PageResult<WalletRecord>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WalletRecord> result = walletService.lambdaQuery()
                .eq(userId != null, WalletRecord::getUserId, userId)
                .eq(type != null, WalletRecord::getType, type)
                .orderByDesc(WalletRecord::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }
}