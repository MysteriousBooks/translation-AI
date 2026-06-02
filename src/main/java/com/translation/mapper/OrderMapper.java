package com.translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.translation.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 使用 SELECT ... FOR UPDATE 加行锁查询订单，避免并发申请退款/支付回调的竞态
     */
    @Select("SELECT * FROM `order` WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    Order selectOrderForUpdate(@Param("id") Long id);

    /**
     * 按订单号加行锁查询，用于支付回调场景的并发安全
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo} AND deleted = 0 FOR UPDATE")
    Order selectOrderByNoForUpdate(@Param("orderNo") String orderNo);

    /**
     * 聚合查询：已支付订单的充值总额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM `order` WHERE status = #{status} AND deleted = 0")
    BigDecimal sumPaidAmount(@Param("status") int status);

    /**
     * 聚合查询：指定时间范围内已支付订单的充值总额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM `order` WHERE status = #{status} AND deleted = 0 AND pay_time >= #{startTime} AND pay_time <= #{endTime}")
    BigDecimal sumPaidAmountByTimeRange(@Param("status") int status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}