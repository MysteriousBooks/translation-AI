package com.translation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;
import com.translation.entity.Feedback;
import com.translation.mapper.FeedbackMapper;
import com.translation.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    public void submitFeedback(Long userId, String content) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(content);
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
    }

    @Override
    public Page<Feedback> getAdminFeedbackPage(Integer status, int page, int size) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        return feedbackMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void replyFeedback(Long feedbackId, String reply) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(ResultCode.FAIL);
        }
        feedback.setReply(reply);
        feedback.setStatus(1);
        feedbackMapper.updateById(feedback);
    }
}