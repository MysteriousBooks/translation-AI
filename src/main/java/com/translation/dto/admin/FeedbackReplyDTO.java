package com.translation.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("反馈回复请求")
public class FeedbackReplyDTO {

    @NotBlank(message = "回复内容不能为空")
    @ApiModelProperty("回复内容")
    private String reply;
}