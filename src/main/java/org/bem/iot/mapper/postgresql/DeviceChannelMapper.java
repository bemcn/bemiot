package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.device.DeviceChannel;

@Mapper
public interface DeviceChannelMapper extends BaseMapper<DeviceChannel> {
    @Select("SELECT COALESCE(MAX(channel_number), 0) FROM device_channel ${ew.customSqlSegment}")
    Integer selectMax(@Param(Constants.WRAPPER) QueryWrapper<DeviceChannel> queryWrapper);
}