package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.entity.TokenStatistics;
import com.translation.service.TokenStatisticsService;
import com.translation.vo.admin.TokenStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "后台-Token统计")
@RestController
@RequestMapping("/api/admin/token")
@RequiredArgsConstructor
public class AdminTokenController {

    private final TokenStatisticsService tokenStatisticsService;

    @ApiOperation("Token统计")
    @GetMapping("/statistics")
    public Result<List<TokenStatisticsVO>> statistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(tokenStatisticsService.getStatistics(startDate, endDate));
    }

    @ApiOperation("统计详情(分页)")
    @GetMapping("/detail")
    public Result<PageResult<TokenStatistics>> detail(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TokenStatistics> result = tokenStatisticsService.getStatisticsPage(startDate, endDate, page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }
}