package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.dto.admin.AnnouncementDTO;
import com.translation.entity.Announcement;
import com.translation.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台-公告管理")
@RestController
@RequestMapping("/api/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AnnouncementService announcementService;

    @ApiOperation("公告列表")
    @GetMapping("/list")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> result = announcementService.getAdminNoticeList(page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("创建公告")
    @PostMapping
    public Result<Void> create(@Validated @RequestBody AnnouncementDTO dto) {
        announcementService.createAnnouncement(dto);
        return Result.success();
    }

    @ApiOperation("修改公告")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AnnouncementDTO dto) {
        announcementService.updateAnnouncement(id, dto);
        return Result.success();
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
}