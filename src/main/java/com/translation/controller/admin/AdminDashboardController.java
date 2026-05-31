package com.translation.controller.admin;

import com.translation.common.result.Result;
import com.translation.service.DashboardService;
import com.translation.vo.admin.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "后台-数据看板")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @ApiOperation("数据概览")
    @GetMapping("/overview")
    public Result<DashboardVO> overview() {
        return Result.success(dashboardService.getOverview());
    }
}