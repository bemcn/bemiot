package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.general.DriveParams;

@Mapper
public interface DriveParamsMapper extends BaseMapper<DriveParams> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM drive_params WHERE drive_code=#{driveCode} and group_type=#{groupType}")
    Integer selectMax(@Param("driveCode") String driveCode, @Param("groupType") Integer groupType);
}
