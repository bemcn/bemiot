package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.system.SystemDictType;
import org.bem.iot.model.system.SystemDictTypeVo;

import java.util.List;

@Mapper
public interface SystemDictTypeMapper extends BaseMapper<SystemDictType> {
    @Select("select dict_type_id,type_name,type_key,(select count(*) from system_dict where system_dict.dict_type_id=system_dict_type.dict_type_id) as dict_count from system_dict_type ${ew.customSqlSegment}")
    List<SystemDictTypeVo> selectByCopies(@Param(Constants.WRAPPER) QueryWrapper<SystemDictType> queryWrapper);

    // 分页查询（仅查询当前页数据）
    @Select("select dict_type_id,type_name,type_key,(select count(*) from system_dict where system_dict.dict_type_id=system_dict_type.dict_type_id) as dict_count from system_dict_type ${ew.customSqlSegment}")
    IPage<SystemDictTypeVo> selectPageByCopies(Page<SystemDictType> page, @Param(Constants.WRAPPER) QueryWrapper<SystemDictType> queryWrapper);
}
