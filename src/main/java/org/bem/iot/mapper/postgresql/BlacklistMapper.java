package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.system.Blacklist;

@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {
}
