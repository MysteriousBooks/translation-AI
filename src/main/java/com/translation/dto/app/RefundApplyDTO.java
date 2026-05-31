package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("退款申请请求")
public class RefundApplyDTO {

    @NotNull(message = "订单ID不能为空")
    @ApiModelProperty("订单ID")
    private Long orderId;

    @NotBlank(message = "退款原因不能为空")
    @ApiModelProperty("退款原因")
    private String reason;
}