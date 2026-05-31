package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@ApiModel("充值请求")
public class RechargeDTO {

    @NotNull(message = "支付类型不能为空")
    @ApiModelProperty("支付类型:1支付宝 2微信")
    private Integer payType;

    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    @ApiModelProperty("充值金额(元)")
    private BigDecimal amount;
}