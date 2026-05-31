package com.translation.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("后台退款响应")
public class AdminRefundVO {

    @ApiModelProperty("退款ID")
    private Long id;

    @ApiModelProperty("退款单号")
    private String refundNo;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户邮箱")
    private String userEmail;

    @ApiModelProperty("退款金额")
    private BigDecimal amount;

    @ApiModelProperty("退款原因")
    private String reason;

    @ApiModelProperty("状态:0待审核 1同意 2拒绝 3退款中 4已完成")
    private Integer status;

    @ApiModelProperty("审核管理员ID")
    private Long adminId;

    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}