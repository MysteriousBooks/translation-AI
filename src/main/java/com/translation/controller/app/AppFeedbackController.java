package com.translation.controller.app;

import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.app.FeedbackDTO;
import com.translation.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "APP端-意见反馈")
@RestController
@RequestMapping("/api/app/feedback")
@RequiredArgsConstructor
public class AppFeedbackController {

    private final FeedbackService feedbackService;

    @ApiOperation("提交反馈")
    @PostMapping
    public Result<Void> submitFeedback(@Validated @RequestBody FeedbackDTO dto) {
        feedbackService.submitFeedback(UserContext.getRequiredUserId(), dto.getContent());
        return Result.success();
    }
}