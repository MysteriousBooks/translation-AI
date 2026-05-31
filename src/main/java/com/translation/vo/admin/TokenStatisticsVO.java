package com.translation.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("Token统计响应")
public class TokenStatisticsVO {

    @ApiModelProperty("统计日期")
    private LocalDate statDate;

    @ApiModelProperty("调用次数")
    private Integer totalCalls;

    @ApiModelProperty("消耗Token数")
    private Integer totalTokens;

    @ApiModelProperty("翻译字符数")
    private Integer totalChars;

    @ApiModelProperty("消耗金额(元)")
    private BigDecimal totalCost;

    @ApiModelProperty("成功次数")
    private Integer successCount;

    @ApiModelProperty("失败次数")
    private Integer failCount;
}