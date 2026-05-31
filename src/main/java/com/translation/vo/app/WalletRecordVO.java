package com.translation.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("钱包流水记录响应")
public class WalletRecordVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("类型:1充值 2消耗 3退款")
    private Integer type;

    @ApiModelProperty("金额(元)")
    private BigDecimal amount;

    @ApiModelProperty("变动前余额")
    private BigDecimal balanceBefore;

    @ApiModelProperty("变动后余额")
    private BigDecimal balanceAfter;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}