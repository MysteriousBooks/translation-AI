package com.translation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("token_statistics")
public class TokenStatistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate statDate;

    private Integer totalCalls;

    private Integer totalTokens;

    private Integer totalChars;

    private BigDecimal totalCost;

    private Integer successCount;

    private Integer failCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}