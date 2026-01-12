package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.user.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM role")
    Integer selectMax();
}
