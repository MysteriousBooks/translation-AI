package com.translation.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("翻译结果响应")
public class TranslateResultVO {

    @ApiModelProperty("翻译记录ID")
    private Long id;

    @ApiModelProperty("源语言")
    private String sourceLang;

    @ApiModelProperty("目标语言")
    private String targetLang;

    @ApiModelProperty("原文")
    private String sourceText;

    @ApiModelProperty("译文")
    private String translatedText;

    @ApiModelProperty("字符数")
    private Integer charCount;

    @ApiModelProperty("消耗金额")
    private BigDecimal costAmount;

    @ApiModelProperty("单价(元/千字符)")
    private BigDecimal pricePerKchar;

    @ApiModelProperty("状态:0翻译中 1成功 2失败")
    private Integer status;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}