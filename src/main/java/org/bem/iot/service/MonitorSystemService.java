package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.MonitorSystemMapper;
import org.bem.iot.mapper.postgresql.SystemConfigMapper;
import org.bem.iot.model.monitor.MonitorSystem;
import org.bem.iot.model.system.SystemConfig;
import org.bem.iot.util.DateUtil;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * 系统监控
 * @author JiangShiYi
 */
@Service
public class MonitorSystemService {
    @Resource
    MonitorSystemMapper monitorSystemMapper;

    @Resource
    SystemConfigMapper systemConfigMapper;

    /**
     * 近60分钟统计(10秒为单位)
     * @return 统计分组与值数组
     */
    public JSONObject rateByHour(String identity) {
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        second = second / 10 * 10;
        calendar.set(Calendar.SECOND, second);
        long endTimer = calendar.getTimeInMillis() / 1000 * 1000;

        calendar.add(Calendar.MINUTE, -60);
        long startTimer = calendar.getTimeInMillis() / 1000 * 1000;

        QueryWrapper<MonitorSystem> example = new QueryWrapper<>();
        example.eq("identity", identity).ge("timestamp", startTimer).lt("timestamp", endTimer);
        example.orderByAsc("timestamp");
        List<MonitorSystem> list = monitorSystemMapper.selectList(example);
        String[] timerArray = new String[360];
        Long[] valueArray = new Long[360];
        for(int i = 0; i < 360; i++) {
            long timer = i * 10000;
            long initTimer = startTimer +  timer;
            MonitorSystem monitorSystem = list.stream().filter(item -> item.getTimestamp() == initTimer).findFirst().orElse(null);
            timerArray[i] = DateUtil.timeToMinute(initTimer);
            if(monitorSystem == null) {
                valueArray[i] = 0L;
            } else {
                valueArray[i] = monitorSystem.getValue();
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("group", timerArray);
        obj.put("data", valueArray);
        return obj;
    }

    /**
     * 写入统计
     * @param identity 分组
     * @param value 值
     */
    public void insert(String identity, long value, long timestamp) {
        String id = InternalIdUtil.createId();
        MonitorSystem record = new MonitorSystem();
        record.setId(id);
        record.setIdentity(identity);
        record.setValue(value);
        record.setTimestamp(timestamp);
        monitorSystemMapper.insert(record);
    }

    /**
     * 删除运用授权信息信息
     */
    public void delOvertime() {
        SystemConfig config = systemConfigMapper.selectById("monitorExpire");
        int monitorExpire = Integer.parseInt(config.getConfigValue());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -monitorExpire);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long timestamp = calendar.getTimeInMillis();


        QueryWrapper<MonitorSystem> example = new QueryWrapper<>();
        example.le("timestamp", timestamp);
        monitorSystemMapper.delete(example);
    }
}
