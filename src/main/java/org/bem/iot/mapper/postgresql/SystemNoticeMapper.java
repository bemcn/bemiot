package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.system.SystemNotice;

@Mapper
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {
}
