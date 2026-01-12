package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.general.Protocols;

@Mapper
public interface ProtocolsMapper extends BaseMapper<Protocols> {
}
