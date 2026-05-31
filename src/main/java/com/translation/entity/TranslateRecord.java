package com.translation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("translate_record")
public class TranslateRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String sourceLang;

    private String targetLang;

    private String sourceText;

    private String translatedText;

    private Integer charCount;

    private BigDecimal costAmount;

    private BigDecimal pricePerKchar;

    private Integer tokenCount;

    private Integer status;

    private String errorMsg;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}