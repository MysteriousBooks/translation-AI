package com.translation.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("订单响应")
public class OrderVO {

    @ApiModelProperty("订单ID")
    private Long id;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("支付类型:1支付宝 2微信")
    private Integer payType;

    @ApiModelProperty("充值金额(元)")
    private BigDecimal amount;

    @ApiModelProperty("状态:0待支付 1已支付 2已取消 3已退款")
    private Integer status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("支付参数(支付宝表单HTML或微信支付链接)")
    private String payData;
}