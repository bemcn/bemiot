package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.general.FirmwareUpdateTask;

@Mapper
public interface FirmwareUpdateTaskMapper extends BaseMapper<FirmwareUpdateTask> {
}
