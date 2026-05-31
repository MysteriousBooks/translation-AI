package com.translation.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("数据看板响应")
public class DashboardVO {

    @ApiModelProperty("今日注册用户数")
    private Integer todayNewUsers;

    @ApiModelProperty("总用户数")
    private Integer totalUsers;

    @ApiModelProperty("今日翻译次数")
    private Integer todayTranslateCount;

    @ApiModelProperty("今日翻译字符数")
    private Integer todayTranslateChars;

    @ApiModelProperty("今日消耗金额")
    private BigDecimal todayConsumeAmount;

    @ApiModelProperty("今日充值金额")
    private BigDecimal todayRechargeAmount;

    @ApiModelProperty("今日Token消耗数")
    private Integer todayTokenCount;

    @ApiModelProperty("总充值金额")
    private BigDecimal totalRechargeAmount;

    @ApiModelProperty("总消耗金额")
    private BigDecimal totalConsumeAmount;
}