package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.monitor.MonitorSystem;

@Mapper
public interface MonitorSystemMapper extends BaseMapper<MonitorSystem> {
}
