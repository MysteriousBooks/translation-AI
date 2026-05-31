package com.translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.translation.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}