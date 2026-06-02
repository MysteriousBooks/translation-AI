package com.translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.translation.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 使用 SELECT ... FOR UPDATE 加行锁查询用户，避免并发扣费/充值竞态
     */
    @Select("SELECT * FROM user WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    User selectUserForUpdate(@Param("id") Long id);

    /**
     * 聚合查询：所有用户的累计消费总额
     */
    @Select("SELECT COALESCE(SUM(total_consume), 0) FROM user WHERE deleted = 0")
    BigDecimal sumTotalConsume();
}