package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.bem.iot.model.device.Device;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    @Select("SELECT COALESCE(MAX(main_channel), 0) FROM device ${ew.customSqlSegment}")
    Integer selectMaxMainChannel(@Param(Constants.WRAPPER) QueryWrapper<Device> queryWrapper);

    @Update("update device set address=#{address},longitude=#{longitude},latitude=#{latitude} where device_id=#{id}")
    void updateLocation(@Param("id") String id, @Param("address") String address, @Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude);

    @Update("update device set firmware_version=#{firmwareVersion} where device_id=#{id}")
    void updateFirmwareVersion(@Param("id") String id, @Param("firmwareVersion") String firmwareVersion);

    @Update("update device set summary=#{summary} where device_id=#{id}")
    void updateSummary(@Param("id") String id, @Param("summary") String summary);

    @Update("update device set status=3, active_time=#{activeTime} where device_id=#{id}")
    void updateActive(@Param("id") String id, @Param("activeTime") Date activeTime);

    @Update("update device set status=#{status} where device_id=#{id}")
    void updateStatus(@Param("id") String id, @Param("status") Integer status);
}
