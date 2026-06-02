package com.translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.translation.entity.TokenStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface TokenStatisticsMapper extends BaseMapper<TokenStatistics> {

    /**
     * 原子累加统计数据，避免并发场景下"读-改-写"导致的计数丢失
     */
    @Update("UPDATE token_statistics SET " +
            "total_calls = total_calls + #{callDelta}, " +
            "total_tokens = total_tokens + #{tokens}, " +
            "total_chars = total_chars + #{chars}, " +
            "total_cost = total_cost + #{cost}, " +
            "success_count = success_count + #{successDelta}, " +
            "fail_count = fail_count + #{failDelta} " +
            "WHERE stat_date = #{date}")
    int incrementStatistics(@Param("date") LocalDate date,
                            @Param("tokens") int tokens,
                            @Param("chars") int chars,
                            @Param("cost") BigDecimal cost,
                            @Param("successDelta") int successDelta,
                            @Param("failDelta") int failDelta,
                            @Param("callDelta") int callDelta);
}