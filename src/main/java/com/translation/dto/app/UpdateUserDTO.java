package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@ApiModel("修改用户信息请求")
public class UpdateUserDTO {

    @Size(max = 64, message = "昵称最长64位")
    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("手机号")
    private String phone;
}