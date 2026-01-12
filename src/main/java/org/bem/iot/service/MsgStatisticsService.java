package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.MsgStatisticsMapper;
import org.bem.iot.model.statistics.MsgStatistics;
import org.bem.iot.model.statistics.MsgStatisticsDay;
import org.bem.iot.model.statistics.MsgStatisticsMonth;
import org.bem.iot.util.DateUtil;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 消息统计
 * @author JiangShiYi
 */
@Service
public class MsgStatisticsService {
    @Resource
    MsgStatisticsMapper msgStatisticsMapper;

    @Resource
    SystemConfigService systemConfigService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取日统计图表数据
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 返回列表
     */
    public JSONObject dayStatisticsChart(int year, int month, int day) {
        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.eq("day", day);
        example.orderByAsc("hour");
        List<MsgStatistics> list = msgStatisticsMapper.selectList(example);
        long[] sendArray = new long[24];
        long[] gatherArray = new long[24];
        long[] alarmArray = new long[24];
        long[] eventArray = new long[24];
        for (int i = 0; i < 24; i++) {
            long sendCount = 0;
            long gatherCount = 0;
            long alarmCount = 0;
            long eventCount = 0;
            for (MsgStatistics statistics : list) {
                if(statistics.getHour() == i) {
                    sendCount = statistics.getSendCount();
                    gatherCount = statistics.getGatherCount();
                    alarmCount = statistics.getAlarmCount();
                    eventCount = statistics.getEventCount();
                    break;
                }
            }
            sendArray[i] = sendCount;
            gatherArray[i] = gatherCount;
            alarmArray[i] = alarmCount;
            eventArray[i] = eventCount;
        }
        JSONObject data = new JSONObject();
        data.put("sends", sendArray);
        data.put("gathers", gatherArray);
        data.put("alarms", alarmArray);
        data.put("events", eventArray);
        return data;
    }

    /**
     * 获取实时统计图表数据(小时)
     * @return 列表
     */
    public JSONObject nowStatisticsChart() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long sendCount = 0;
        long gatherCount = 0;
        long alarmCount = 0;
        long eventCount = 0;

        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.eq("day", day);
        example.eq("hour", hour);
        if(msgStatisticsMapper.exists(example)) {
            MsgStatistics statistics = msgStatisticsMapper.selectOne(example);
            sendCount = statistics.getSendCount();
            gatherCount = statistics.getGatherCount();
            alarmCount = statistics.getAlarmCount();
            eventCount = statistics.getEventCount();
        }
        JSONObject data = new JSONObject();
        data.put("hour", hour);
        data.put("sendCount", sendCount);
        data.put("gatherCount", gatherCount);
        data.put("alarmCount", alarmCount);
        data.put("eventCount", eventCount);
        return data;
    }

    /**
     * 查询日统计明细
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 返回列表
     */
    public List<MsgStatistics> selectByDay(int year, int month, int day) {
        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.eq("day", day);
        example.orderByAsc("hour");
        List<MsgStatistics> list = msgStatisticsMapper.selectList(example);
        return supplementDayList(year, month, day, list);
    }
    private List<MsgStatistics> supplementDayList(int year, int month, int day, List<MsgStatistics> statistList) {
        List<MsgStatistics> list = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            boolean isExist = false;
            MsgStatistics record = new MsgStatistics();
            for (MsgStatistics statistics : statistList) {
                if(statistics.getHour() == i) {
                    isExist = true;
                    record = statistics;
                    break;
                }
            }
            if(!isExist) {
                record = defaultHour(year, month, day, i);
            }
            list.add(record);
        }
        return list;
    }
    private MsgStatistics defaultHour(int year, int month, int day, int hour) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month, day, hour);

        MsgStatistics record = new MsgStatistics();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);
        record.setDay(day);
        record.setHour(hour);
        record.setSendCount(0L);
        record.setGatherCount(0L);
        record.setAlarmCount(0L);
        record.setEventCount(0L);
        record.setAuthCount(0L);
        record.setConnectCount(0L);
        record.setSubscribeCount(0L);
        record.setRoutingCount(0L);
        record.setRetainCount(0L);
        record.setConverCount(0L);
        return record;
    }

    /**
     * 查询月统计明细
     * @param year 年
     * @param month 月
     * @param index 页码
     * @param size 每页数量
     * @return 返回列表
     */
    public List<MsgStatisticsDay> selectByMonth(int year, int month, long index, long size) {
        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.orderByAsc("day");
        List<MsgStatistics> list = msgStatisticsMapper.selectList(example);
        return supplementMonthList(year, month, list);
    }
    private List<MsgStatisticsDay> supplementMonthList(int year, int month, List<MsgStatistics> statistList) {
        int dayCount = DateUtil.getMonthDayCount(year, month);
        List<MsgStatisticsDay> list = new ArrayList<>();
        List<MsgStatistics> dayList = new ArrayList<>();
        for(int i = 1; i < dayCount; i++) {
            MsgStatisticsDay record;
            for (MsgStatistics statistics : statistList) {
                if(statistics.getDay() == i) {
                    dayList.add(statistics);
                }
            }

            if(!dayList.isEmpty()) {
                record = formatDay(year, month, i, dayList);
            } else {
                record = defaultDay(year, month, i);
            }
            list.add(record);
        }
        return list;
    }
    private MsgStatisticsDay formatDay(int year, int month, int day, List<MsgStatistics> statistList) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month, day);

        MsgStatisticsDay record = new MsgStatisticsDay();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);
        record.setDay(day);

        long sendCount = 0L;
        long gatherCount = 0L;
        long alarmCount = 0L;
        long eventCount = 0L;
        long authCount = 0L;
        long connectCount = 0L;
        long subscribeCount = 0L;
        long routingCount = 0L;
        long retainCount = 0L;
        long converCount = 0L;
        for (MsgStatistics statistics : statistList) {
            sendCount = sendCount + statistics.getSendCount();
            gatherCount = gatherCount + statistics.getGatherCount();
            alarmCount = alarmCount + statistics.getAlarmCount();
            eventCount = eventCount + statistics.getEventCount();
            authCount = authCount + statistics.getAuthCount();
            connectCount = connectCount + statistics.getConnectCount();
            subscribeCount = subscribeCount + statistics.getSubscribeCount();
            routingCount = statistics.getRoutingCount();
            retainCount = statistics.getRetainCount();
            converCount = statistics.getConverCount();
        }

        record.setSendCount(sendCount);
        record.setGatherCount(gatherCount);
        record.setAlarmCount(alarmCount);
        record.setEventCount(eventCount);
        record.setAuthCount(authCount);
        record.setConnectCount(connectCount);
        record.setSubscribeCount(subscribeCount);
        record.setRoutingCount(routingCount);
        record.setRetainCount(retainCount);
        record.setConverCount(converCount);
        return record;
    }
    private MsgStatisticsDay defaultDay(int year, int month, int day) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month, day);

        MsgStatisticsDay record = new MsgStatisticsDay();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);
        record.setDay(day);
        record.setSendCount(0L);
        record.setGatherCount(0L);
        record.setAlarmCount(0L);
        record.setEventCount(0L);
        record.setAuthCount(0L);
        record.setConnectCount(0L);
        record.setSubscribeCount(0L);
        record.setRoutingCount(0L);
        record.setRetainCount(0L);
        record.setConverCount(0L);
        return record;
    }

    /**
     * 查询实时总计
     * @return 实时总计
     */
    public MsgStatistics findRealTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return realTimeData(year, month, day, hour);
    }
    private MsgStatistics realTimeData(int year, int month, int day, int hour) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month, day, hour);

        String strSendCount = stringRedisTemplate.opsForValue().get("msg::statistics::sendCount");
        String strGatherCount = stringRedisTemplate.opsForValue().get("msg::statistics::gatherCount");
        String strAlarmCount = stringRedisTemplate.opsForValue().get("msg::statistics::alarmCount");
        String strEventCount = stringRedisTemplate.opsForValue().get("msg::statistics::eventCount");
        String strAuthCount = stringRedisTemplate.opsForValue().get("msg::statistics::authCount");
        String strConnectCount = stringRedisTemplate.opsForValue().get("msg::statistics::connectCount");
        String strSubscribeCount = stringRedisTemplate.opsForValue().get("msg::statistics::subscribeCount");
        String strRoutingCount = stringRedisTemplate.opsForValue().get("msg::statistics::routingCount");
        String strRetainCount = stringRedisTemplate.opsForValue().get("msg::statistics::retainCount");
        String strConverCount= stringRedisTemplate.opsForValue().get("msg::statistics::converCount");

        long sendCount = strSendCount == null ? 0L : Long.parseLong(strSendCount);
        long gatherCount = strGatherCount == null ? 0L : Long.parseLong(strGatherCount);
        long alarmCount = strAlarmCount == null ? 0L : Long.parseLong(strAlarmCount);
        long eventCount = strEventCount == null ? 0L : Long.parseLong(strEventCount);
        long authCount = strAuthCount == null ? 0L : Long.parseLong(strAuthCount);
        long connectCount = strConnectCount == null ? 0L : Long.parseLong(strConnectCount);
        long subscribeCount = strSubscribeCount == null ? 0L : Long.parseLong(strSubscribeCount);
        long routingCount = strRoutingCount == null ? 0L : Long.parseLong(strRoutingCount);
        long retainCount = strRetainCount == null ? 0L : Long.parseLong(strRetainCount);
        long converCount = strConverCount == null ? 0L : Long.parseLong(strConverCount);

        MsgStatistics record = new MsgStatistics();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);
        record.setDay(day);
        record.setHour(hour);
        record.setSendCount(sendCount);
        record.setGatherCount(gatherCount);
        record.setAlarmCount(alarmCount);
        record.setEventCount(eventCount);
        record.setAuthCount(authCount);
        record.setConnectCount(connectCount);
        record.setSubscribeCount(subscribeCount);
        record.setRoutingCount(routingCount);
        record.setRetainCount(retainCount);
        record.setConverCount(converCount);
        return record;
    }

    /**
     * 查询小时总计
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 小时
     * @return 小时总计
     */
    public MsgStatistics findHour(int year, int month, int day, int hour) {
        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.eq("day", day);
        example.eq("hour", hour);
        if(msgStatisticsMapper.exists(example)) {
            return msgStatisticsMapper.selectOne(example);
        } else {
            return defaultHour(year, month, day, hour);
        }
    }

    /**
     * 查询日总计
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 日总计
     */
    public MsgStatisticsDay findDay(int year, int month, int day) {
        MsgStatisticsDay record;

        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        example.eq("day", day);
        if(msgStatisticsMapper.exists(example)) {
            List<MsgStatistics> list = msgStatisticsMapper.selectList(example);
            record = formatDay(year, month, day, list);
        } else {
            record = defaultDay(year, month, day);
        }
        return record;
    }

    /**
     * 查询月总计
     * @param year 年
     * @param month 月
     * @return 月总计
     */
    public MsgStatisticsMonth findMonth(int year, int month) {
        MsgStatisticsMonth record;

        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("year", year);
        example.eq("month", month);
        if(msgStatisticsMapper.exists(example)) {
            List<MsgStatistics> list = msgStatisticsMapper.selectList(example);
            record = formatMonth(year, month, list);
        } else {
            record = defaultMonth(year, month);
        }
        return record;
    }
    private MsgStatisticsMonth formatMonth(int year, int month, List<MsgStatistics> statistList) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month);

        MsgStatisticsMonth record = new MsgStatisticsMonth();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);

        long sendCount = 0L;
        long gatherCount = 0L;
        long alarmCount = 0L;
        long eventCount = 0L;
        long authCount = 0L;
        long connectCount = 0L;
        long subscribeCount = 0L;
        long routingCount = 0L;
        long retainCount = 0L;
        long converCount = 0L;
        for (MsgStatistics statistics : statistList) {
            sendCount = sendCount + statistics.getSendCount();
            gatherCount = gatherCount + statistics.getGatherCount();
            alarmCount = alarmCount + statistics.getAlarmCount();
            eventCount = eventCount + statistics.getEventCount();
            authCount = authCount + statistics.getAuthCount();
            connectCount = connectCount + statistics.getConnectCount();
            subscribeCount = subscribeCount + statistics.getSubscribeCount();
            routingCount = statistics.getRoutingCount();
            retainCount = statistics.getRetainCount();
            converCount = statistics.getConverCount();
        }

        record.setSendCount(sendCount);
        record.setGatherCount(gatherCount);
        record.setAlarmCount(alarmCount);
        record.setEventCount(eventCount);
        record.setAuthCount(authCount);
        record.setConnectCount(connectCount);
        record.setSubscribeCount(subscribeCount);
        record.setRoutingCount(routingCount);
        record.setRetainCount(retainCount);
        record.setConverCount(converCount);
        return record;
    }
    private MsgStatisticsMonth defaultMonth(int year, int month) {
        int statisticsId = InternalIdUtil.createStatisticsId(year, month);

        MsgStatisticsMonth record = new MsgStatisticsMonth();
        record.setStatisticsId(statisticsId);
        record.setYear(year);
        record.setMonth(month);
        record.setSendCount(0L);
        record.setGatherCount(0L);
        record.setAlarmCount(0L);
        record.setEventCount(0L);
        record.setAuthCount(0L);
        record.setConnectCount(0L);
        record.setSubscribeCount(0L);
        record.setRoutingCount(0L);
        record.setRetainCount(0L);
        record.setConverCount(0L);
        return record;
    }

    /**
     * 保存统计
     */
    public void saveData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        MsgStatistics record = realTimeData(year, month, day, hour);
        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.eq("statistics_id", record.getStatisticsId());
        if (msgStatisticsMapper.exists(example)) {
            msgStatisticsMapper.update(record, example);
        } else {
            msgStatisticsMapper.insert(record);
        }
    }

    /**
     * 删除过期数据
     */
    public void delByOverdue() {
        String monitorExpire = systemConfigService.find("monitorExpire");
        int months = Integer.parseInt(monitorExpire);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int statisticsId = InternalIdUtil.createStatisticsId(year, month, day, hour);

        QueryWrapper<MsgStatistics> example = new QueryWrapper<>();
        example.lt("statistics_id", statisticsId);
        msgStatisticsMapper.delete(example);
    }
}
