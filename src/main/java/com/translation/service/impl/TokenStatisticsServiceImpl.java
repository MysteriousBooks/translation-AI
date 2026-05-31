package com.translation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.entity.TokenStatistics;
import com.translation.mapper.TokenStatisticsMapper;
import com.translation.service.TokenStatisticsService;
import com.translation.vo.admin.TokenStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenStatisticsServiceImpl extends ServiceImpl<TokenStatisticsMapper, TokenStatistics> implements TokenStatisticsService {

    private final TokenStatisticsMapper tokenStatisticsMapper;

    @Override
    public void recordStatistics(int tokens, int chars, BigDecimal cost, boolean success) {
        LocalDate today = LocalDate.now();
        TokenStatistics stats = lambdaQuery()
                .eq(TokenStatistics::getStatDate, today)
                .one();

        if (stats == null) {
            stats = new TokenStatistics();
            stats.setStatDate(today);
            stats.setTotalCalls(1);
            stats.setTotalTokens(tokens);
            stats.setTotalChars(chars);
            stats.setTotalCost(cost);
            stats.setSuccessCount(success ? 1 : 0);
            stats.setFailCount(success ? 0 : 1);
            tokenStatisticsMapper.insert(stats);
        } else {
            stats.setTotalCalls(stats.getTotalCalls() + 1);
            stats.setTotalTokens(stats.getTotalTokens() + tokens);
            stats.setTotalChars(stats.getTotalChars() + chars);
            stats.setTotalCost(stats.getTotalCost().add(cost));
            if (success) {
                stats.setSuccessCount(stats.getSuccessCount() + 1);
            } else {
                stats.setFailCount(stats.getFailCount() + 1);
            }
            tokenStatisticsMapper.updateById(stats);
        }
    }

    @Override
    public List<TokenStatisticsVO> getStatistics(LocalDate startDate, LocalDate endDate) {
        List<TokenStatistics> list = lambdaQuery()
                .ge(TokenStatistics::getStatDate, startDate)
                .le(TokenStatistics::getStatDate, endDate)
                .orderByAsc(TokenStatistics::getStatDate)
                .list();

        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Page<TokenStatistics> getStatisticsPage(LocalDate startDate, LocalDate endDate, int page, int size) {
        LambdaQueryWrapper<TokenStatistics> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(TokenStatistics::getStatDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(TokenStatistics::getStatDate, endDate);
        }
        wrapper.orderByDesc(TokenStatistics::getStatDate);
        return tokenStatisticsMapper.selectPage(new Page<>(page, size), wrapper);
    }

    private TokenStatisticsVO toVO(TokenStatistics stats) {
        TokenStatisticsVO vo = new TokenStatisticsVO();
        BeanUtil.copyProperties(stats, vo);
        return vo;
    }
}