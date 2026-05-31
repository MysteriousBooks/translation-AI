package com.translation.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.entity.Announcement;
import com.translation.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "APP端-通知公告")
@RestController
@RequestMapping("/api/app/notice")
@RequiredArgsConstructor
public class AppNoticeController {

    private final AnnouncementService announcementService;

    @ApiOperation("公告列表")
    @GetMapping("/list")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> result = announcementService.getAppNoticeList(page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }
}