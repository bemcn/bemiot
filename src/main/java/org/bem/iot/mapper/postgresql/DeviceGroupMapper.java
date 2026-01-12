package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.device.DeviceGroup;

@Mapper
public interface DeviceGroupMapper extends BaseMapper<DeviceGroup> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM device_group")
    Integer selectMax();
}
