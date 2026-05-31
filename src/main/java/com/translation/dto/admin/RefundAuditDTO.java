package com.translation.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("退款审核请求")
public class RefundAuditDTO {

    @NotBlank(message = "审核备注不能为空")
    @ApiModelProperty("审核备注")
    private String auditRemark;
}