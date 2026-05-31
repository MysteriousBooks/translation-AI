package com.translation.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("管理员登录响应")
public class AdminLoginVO {

    @ApiModelProperty("Token")
    private String token;

    @ApiModelProperty("管理员ID")
    private Long adminId;

    @ApiModelProperty("昵称")
    private String nickname;
}