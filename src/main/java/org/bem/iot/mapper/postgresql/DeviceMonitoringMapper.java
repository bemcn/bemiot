package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.device.DeviceMonitoring;

@Mapper
public interface DeviceMonitoringMapper extends BaseMapper<DeviceMonitoring> {
}
