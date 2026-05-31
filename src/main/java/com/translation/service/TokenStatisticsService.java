package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.entity.TokenStatistics;
import com.translation.vo.admin.TokenStatisticsVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TokenStatisticsService extends IService<TokenStatistics> {

    void recordStatistics(int tokens, int chars, BigDecimal cost, boolean success);

    List<TokenStatisticsVO> getStatistics(LocalDate startDate, LocalDate endDate);

    Page<TokenStatistics> getStatisticsPage(LocalDate startDate, LocalDate endDate, int page, int size);
}