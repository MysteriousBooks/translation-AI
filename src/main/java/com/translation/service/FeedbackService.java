package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {

    void submitFeedback(Long userId, String content);

    Page<Feedback> getAdminFeedbackPage(Integer status, int page, int size);

    void replyFeedback(Long feedbackId, String reply);
}