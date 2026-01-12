package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.scene.SceneGroup;

@Mapper
public interface SceneGroupMapper extends BaseMapper<SceneGroup> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM scene_group")
    Integer selectMax();
}
