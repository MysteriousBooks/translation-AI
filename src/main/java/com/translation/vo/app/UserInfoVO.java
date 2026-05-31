package com.translation.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("用户信息响应")
public class UserInfoVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("登录类型:1邮箱 2微信 3支付宝")
    private Integer loginType;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("累计消耗")
    private BigDecimal totalConsume;

    @ApiModelProperty("注册时间")
    private LocalDateTime createTime;
}