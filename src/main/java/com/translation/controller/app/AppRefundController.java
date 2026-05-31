package com.translation.controller.app;

import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.app.RefundApplyDTO;
import com.translation.service.RefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "APP端-退款")
@RestController
@RequestMapping("/api/app/refund")
@RequiredArgsConstructor
public class AppRefundController {

    private final RefundService refundService;

    @ApiOperation("申请退款")
    @PostMapping("/apply")
    public Result<Void> applyRefund(@Validated @RequestBody RefundApplyDTO dto) {
        refundService.applyRefund(UserContext.getRequiredUserId(), dto);
        return Result.success();
    }
}