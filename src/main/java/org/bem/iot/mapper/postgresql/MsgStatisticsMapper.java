package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.statistics.MsgStatistics;

@Mapper
public interface MsgStatisticsMapper extends BaseMapper<MsgStatistics> {
}
