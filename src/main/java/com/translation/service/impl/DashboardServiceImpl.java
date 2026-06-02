package com.translation.service.impl;

import com.translation.common.enums.OrderStatus;
import com.translation.entity.Order;
import com.translation.entity.TokenStatistics;
import com.translation.entity.User;
import com.translation.mapper.OrderMapper;
import com.translation.mapper.UserMapper;
import com.translation.service.*;
import com.translation.vo.admin.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserService userService;
    private final OrderService orderService;
    private final TranslateService translateService;
    private final TokenStatisticsService tokenStatisticsService;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public DashboardVO getOverview() {
        DashboardVO vo = new DashboardVO();

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        /* 今日注册用户数 */
        long todayNewUsers = userService.lambdaQuery()
                .ge(User::getCreateTime, todayStart)
                .le(User::getCreateTime, todayEnd)
                .count();
        vo.setTodayNewUsers((int) todayNewUsers);

        /* 总用户数 */
        long totalUsers = userService.lambdaQuery().count();
        vo.setTotalUsers((int) totalUsers);

        /* 今日翻译次数 */
        long todayTranslateCount = translateService.lambdaQuery()
                .ge(com.translation.entity.TranslateRecord::getCreateTime, todayStart)
                .le(com.translation.entity.TranslateRecord::getCreateTime, todayEnd)
                .count();
        vo.setTodayTranslateCount((int) todayTranslateCount);

        /* 今日统计数据 */
        TokenStatistics todayStats = tokenStatisticsService.lambdaQuery()
                .eq(TokenStatistics::getStatDate, today)
                .one();
        if (todayStats != null) {
            vo.setTodayTranslateChars(todayStats.getTotalChars());
            vo.setTodayConsumeAmount(todayStats.getTotalCost());
            vo.setTodayTokenCount(todayStats.getTotalTokens());
        } else {
            vo.setTodayTranslateChars(0);
            vo.setTodayConsumeAmount(BigDecimal.ZERO);
            vo.setTodayTokenCount(0);
        }

        /* 今日充值金额 - 使用SQL聚合避免全表加载 */
        BigDecimal todayRecharge = orderMapper.sumPaidAmountByTimeRange(OrderStatus.PAID.getCode(), todayStart, todayEnd);
        vo.setTodayRechargeAmount(todayRecharge);

        /* 总充值金额 - 使用SQL聚合避免全表加载 */
        BigDecimal totalRecharge = orderMapper.sumPaidAmount(OrderStatus.PAID.getCode());
        vo.setTotalRechargeAmount(totalRecharge);

        /* 总消耗金额 - 使用SQL聚合避免全表加载 */
        BigDecimal totalConsume = userMapper.sumTotalConsume();
        vo.setTotalConsumeAmount(totalConsume);

        return vo;
    }
}