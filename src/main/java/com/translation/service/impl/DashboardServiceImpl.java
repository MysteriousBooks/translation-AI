package com.translation.service.impl;

import com.translation.entity.Order;
import com.translation.entity.TokenStatistics;
import com.translation.entity.TranslateRecord;
import com.translation.entity.User;
import com.translation.service.*;
import com.translation.vo.admin.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserService userService;
    private final OrderService orderService;
    private final TranslateService translateService;
    private final TokenStatisticsService tokenStatisticsService;

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
                .ge(TranslateRecord::getCreateTime, todayStart)
                .le(TranslateRecord::getCreateTime, todayEnd)
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

        /* 今日充值金额 */
        List<Order> todayPaidOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, 1)
                .ge(Order::getPayTime, todayStart)
                .le(Order::getPayTime, todayEnd)
                .select(Order::getAmount)
                .list();
        vo.setTodayRechargeAmount(todayPaidOrders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        /* 总充值金额 */
        List<Order> allPaidOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, 1)
                .select(Order::getAmount)
                .list();
        vo.setTotalRechargeAmount(allPaidOrders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        /* 总消耗金额 */
        List<User> users = userService.lambdaQuery()
                .select(User::getTotalConsume)
                .list();
        vo.setTotalConsumeAmount(users.stream()
                .map(User::getTotalConsume)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return vo;
    }
}