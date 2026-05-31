package com.translation.controller.admin;

import com.translation.common.result.Result;
import com.translation.entity.SysConfig;
import com.translation.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台-系统配置")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminConfigController {

    private final SysConfigService sysConfigService;

    @ApiOperation("配置列表")
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        return Result.success(sysConfigService.getAllConfigs());
    }

    @ApiOperation("修改配置")
    @PutMapping("/{key}")
    public Result<Void> update(@PathVariable String key, @RequestBody String value) {
        sysConfigService.updateConfigValue(key, value);
        return Result.success();
    }
}