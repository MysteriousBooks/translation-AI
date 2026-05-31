package com.translation.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("钱包响应")
public class WalletVO {

    @ApiModelProperty("余额(元)")
    private BigDecimal balance;

    @ApiModelProperty("累计消耗(元)")
    private BigDecimal totalConsume;
}