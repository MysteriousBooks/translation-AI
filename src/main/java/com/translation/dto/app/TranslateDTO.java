package com.translation.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel("翻译请求")
public class TranslateDTO {

    @NotBlank(message = "源语言不能为空")
    @ApiModelProperty("源语言")
    private String sourceLang;

    @NotBlank(message = "目标语言不能为空")
    @ApiModelProperty("目标语言")
    private String targetLang;

    @NotBlank(message = "原文不能为空")
    @Size(max = 5000, message = "原文最大5000字符")
    @ApiModelProperty("原文")
    private String sourceText;
}