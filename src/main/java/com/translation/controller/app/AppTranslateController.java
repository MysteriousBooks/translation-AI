package com.translation.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.app.TranslateDTO;
import com.translation.service.TranslateService;
import com.translation.vo.app.TranslateResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "APP端-翻译")
@RestController
@RequestMapping("/api/app/translate")
@RequiredArgsConstructor
public class AppTranslateController {

    private final TranslateService translateService;

    @ApiOperation("提交翻译")
    @PostMapping
    public Result<TranslateResultVO> translate(@Validated @RequestBody TranslateDTO dto) {
        return Result.success(translateService.translate(UserContext.getRequiredUserId(), dto));
    }

    @ApiOperation("翻译历史")
    @GetMapping("/history")
    public Result<PageResult<TranslateResultVO>> history(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TranslateResultVO> result = translateService.getTranslateHistory(UserContext.getRequiredUserId(), page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("翻译详情")
    @GetMapping("/{id}")
    public Result<TranslateResultVO> detail(@PathVariable Long id) {
        return Result.success(translateService.getTranslateDetail(UserContext.getRequiredUserId(), id));
    }
}