package com.translation.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("公告请求")
public class AnnouncementDTO {

    @NotBlank(message = "标题不能为空")
    @ApiModelProperty("标题")
    private String title;

    @NotBlank(message = "内容不能为空")
    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("类型:1通知 2公告")
    private Integer type;

    @ApiModelProperty("状态:0草稿 1已发布")
    private Integer status;
}