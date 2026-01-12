package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.general.SpacePosition;

@Mapper
public interface SpacePositionMapper extends BaseMapper<SpacePosition> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM spatial_position where level_id=#{levelId}")
    Integer selectMax(@Param("levelId") Integer levelId);
}
