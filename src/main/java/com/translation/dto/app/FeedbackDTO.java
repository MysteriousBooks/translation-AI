package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("意见反馈请求")
public class FeedbackDTO {

    @NotBlank(message = "反馈内容不能为空")
    @ApiModelProperty("反馈内容")
    private String content;
}