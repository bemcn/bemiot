package org.bem.iot.mapper.tdengine;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.log.LogModelAlarm;

import java.util.List;
import java.util.Map;

@Mapper
public interface LogModelAlarmMapper extends BaseMapper<LogModelAlarm> {
    @Select("select device_id, count(device_id) as total from bemcn.log_model_alarm where alarm_status=1 group by device_id order by count(device_id) desc limit ${size}")
    List<Map<String, Object>> queryGroupByDevice(@Param("size") Integer size);
}
