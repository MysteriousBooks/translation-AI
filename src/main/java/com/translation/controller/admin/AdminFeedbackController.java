package com.translation.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.dto.admin.FeedbackReplyDTO;
import com.translation.entity.Feedback;
import com.translation.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台-反馈管理")
@RestController
@RequestMapping("/api/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @ApiOperation("反馈列表")
    @GetMapping("/list")
    public Result<PageResult<Feedback>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Feedback> result = feedbackService.getAdminFeedbackPage(status, page, size);
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("回复反馈")
    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @Validated @RequestBody FeedbackReplyDTO dto) {
        feedbackService.replyFeedback(id, dto.getReply());
        return Result.success();
    }
}