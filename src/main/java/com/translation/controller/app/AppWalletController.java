package com.translation.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.app.RechargeDTO;
import com.translation.service.OrderService;
import com.translation.service.WalletService;
import com.translation.vo.app.OrderVO;
import com.translation.vo.app.WalletRecordVO;
import com.translation.vo.app.WalletVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "APP端-钱包")
@RestController
@RequestMapping("/api/app/wallet")
@RequiredArgsConstructor
public class AppWalletController {

    private final WalletService walletService;
    private final OrderService orderService;

    @ApiOperation("余额查询")
    @GetMapping("/balance")
    public Result<WalletVO> balance() {
        return Result.success(walletService.getWalletInfo(UserContext.getRequiredUserId()));
    }

    @ApiOperation("钱包流水")
    @GetMapping("/records")
    public Result<PageResult<WalletRecordVO>> records(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WalletRecordVO> result = walletService.getWalletRecords(UserContext.getRequiredUserId(), type, page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("创建充值订单")
    @PostMapping("/recharge")
    public Result<OrderVO> recharge(@Validated @RequestBody RechargeDTO dto) {
        return Result.success(orderService.createRechargeOrder(UserContext.getRequiredUserId(), dto));
    }

    @ApiOperation("支付宝回调")
    @PostMapping("/recharge/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        orderService.handleAlipayCallback(params);
        return "success";
    }

    @ApiOperation("微信回调")
    @PostMapping("/recharge/callback/wechat")
    public String wechatCallback(@RequestBody String body, HttpServletRequest request) {
        Map<String, String> params = new java.util.HashMap<>();
        params.put("Wechatpay-Timestamp", request.getHeader("Wechatpay-Timestamp"));
        params.put("Wechatpay-Nonce", request.getHeader("Wechatpay-Nonce"));
        params.put("Wechatpay-Signature", request.getHeader("Wechatpay-Signature"));
        params.put("Wechatpay-Serial", request.getHeader("Wechatpay-Serial"));
        params.put("body", body);
        orderService.handleWechatCallback(params);
        return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
    }

    @ApiOperation("查询支付状态")
    @GetMapping("/recharge/status/{orderNo}")
    public Result<OrderVO> rechargeStatus(@PathVariable String orderNo) {
        return Result.success(orderService.getOrderStatus(UserContext.getRequiredUserId(), orderNo));
    }
}