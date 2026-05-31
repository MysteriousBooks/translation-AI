package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("第三方登录请求")
public class SocialLoginDTO {

    @NotBlank(message = "授权码不能为空")
    @ApiModelProperty("授权码")
    private String code;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像URL")
    private String avatar;
}