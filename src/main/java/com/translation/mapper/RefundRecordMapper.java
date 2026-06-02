package com.translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.translation.entity.RefundRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RefundRecordMapper extends BaseMapper<RefundRecord> {

    /**
     * 使用 SELECT ... FOR UPDATE 加行锁查询退款记录，避免并发审核
     */
    @Select("SELECT * FROM refund_record WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    RefundRecord selectRefundForUpdate(@Param("id") Long id);
}