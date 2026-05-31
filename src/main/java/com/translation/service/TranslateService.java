package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.app.TranslateDTO;
import com.translation.entity.TranslateRecord;
import com.translation.vo.app.TranslateResultVO;

public interface TranslateService extends IService<TranslateRecord> {

    TranslateResultVO translate(Long userId, TranslateDTO dto);

    Page<TranslateResultVO> getTranslateHistory(Long userId, int page, int size);

    TranslateResultVO getTranslateDetail(Long userId, Long id);
}