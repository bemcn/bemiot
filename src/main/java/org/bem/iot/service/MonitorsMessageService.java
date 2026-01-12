package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.global.MessageMonitorGlobal;
import org.bem.iot.mapper.tdengine.LogModelAlarmMapper;
import org.bem.iot.mapper.tdengine.StatisticsConnectMessageMapper;
import org.bem.iot.mapper.tdengine.StatisticsOptionMessageMapper;
import org.bem.iot.mapper.tdengine.StatisticsQueueMessageMapper;
import org.bem.iot.model.log.LogModelAlarm;
import org.bem.iot.model.monitor.StatisticsConnectMessage;
import org.bem.iot.model.monitor.StatisticsOptionMessage;
import org.bem.iot.model.monitor.StatisticsQueueMessage;
import org.bem.iot.util.InternalIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MonitorsMessageService {
    @Resource
    RabbitMQInfoService rabbitMQInfoService;

    @Resource
    StatisticsQueueMessageMapper statisticsQueueMessageMapper;

    @Resource
    StatisticsConnectMessageMapper statisticsConnectMessageMapper;

    @Resource
    StatisticsOptionMessageMapper statisticsOptionMessageMapper;

    @Resource
    LogModelAlarmMapper logModelAlarmMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void saveQueueMessage() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        JSONObject cumulativeStats = rabbitMQInfoService.getQueueCumulativeStats();
        long declaredNumber = 0L;
        long createdNumber = 0L;
        long deletedNumber = 0L;
        if (cumulativeStats != null) {
            declaredNumber = Long.parseLong(cumulativeStats.get("declared").toString());
            createdNumber = Long.parseLong(cumulativeStats.get("created").toString());
            deletedNumber = Long.parseLong(cumulativeStats.get("deleted").toString());
        } else {
            logger.error("未能获取RabbitMQ队列累计统计信息");
        }
        String id = InternalIdUtil.createId();

        StatisticsQueueMessage data = new StatisticsQueueMessage();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setDeclaredNumber(declaredNumber);
        data.setCreatedNumber(createdNumber);
        data.setDeletedNumber(deletedNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsQueueMessageMapper.insert(data);
    }

    public void countConnect() {
        JSONObject clientStats = rabbitMQInfoService.getClientConnectionStats();
        if (clientStats != null) {
            long connect = Long.parseLong(clientStats.get("active_connections").toString());
            long lastConnect = MessageMonitorGlobal.lastConnect;
            if(lastConnect > connect) {
                long offline = lastConnect - connect;
                long oldOffline = MessageMonitorGlobal.offline;
                MessageMonitorGlobal.offline = oldOffline + offline;
            } else if(lastConnect < connect) {
                long offline = connect - lastConnect;
                long oldConnect = MessageMonitorGlobal.connect;
                MessageMonitorGlobal.connect = oldConnect + offline;
            }
        }
    }

    public void saveConnectMessage() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        long connectNumber = MessageMonitorGlobal.connect;
        long offlineNumber = MessageMonitorGlobal.offline;
        MessageMonitorGlobal.connect = 0L;
        MessageMonitorGlobal.offline = 0L;
        String id = InternalIdUtil.createId();

        StatisticsConnectMessage data = new StatisticsConnectMessage();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setConnectNumber(connectNumber);
        data.setOfflineNumber(offlineNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsConnectMessageMapper.insert(data);
    }

    public void saveOptionMessage() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        long receivedNumber = MessageMonitorGlobal.received;
        long sendNumber = MessageMonitorGlobal.send;
        long eventNumber = MessageMonitorGlobal.event;
        MessageMonitorGlobal.received = 0L;
        MessageMonitorGlobal.send = 0L;
        MessageMonitorGlobal.event = 0L;
        long alarmNumber = getAlarmHourCount();
        String id = InternalIdUtil.createId();

        StatisticsOptionMessage data = new StatisticsOptionMessage();
        data.setTs(System.currentTimeMillis());
        data.setLogId(id);
        data.setReceivedNumber(receivedNumber);
        data.setSendNumber(sendNumber);
        data.setEventNumber(eventNumber);
        data.setAlarmNumber(alarmNumber);
        data.setYear(year);
        data.setMonth(month);
        data.setDay(day);
        data.setHour(hour);
        statisticsOptionMessageMapper.insert(data);
    }
    private long getAlarmHourCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long endTime = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = calendar.getTimeInMillis();

        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.between("ts", startTime, endTime);
        example.eq("alarm_status", 1);
        return logModelAlarmMapper.selectCount(example);
    }
    private long getAlarmHourCount(Calendar calendar, int hour) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        long endTime = calendar.getTimeInMillis();

        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.between("ts", startTime, endTime);
        example.eq("alarm_status", 1);
        return logModelAlarmMapper.selectCount(example);
    }
    private long getAlarmDayCount(Calendar calendar, int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endTime = calendar.getTimeInMillis();

        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.between("ts", startTime, endTime);
        example.eq("alarm_status", 1);
        return logModelAlarmMapper.selectCount(example);
    }

    /**
     * 消息分类日统计
     * @return 返回统计结果
     */
    public JSONObject statisticsClassDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long[] receivedData = new long[24];
        long[] sendData = new long[24];
        long[] eventData = new long[24];
        long[] alarmData = new long[24];
        String[] xAxis = new String[24];
        for (int i = 0; i < 24; i++) {
            xAxis[i] = i + ":00";

            QueryWrapper<StatisticsOptionMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            example.eq("hour", i);
            long count = statisticsOptionMessageMapper.selectCount(example);
            long receivedNumber = 0L;
            long sendNumber = 0L;
            long eventNumber = 0L;
            long alarmNumber = 0L;
            if(count > 0L) {
                StatisticsOptionMessage data = statisticsOptionMessageMapper.selectOne(example);
                receivedNumber = data.getReceivedNumber();
                sendNumber = data.getSendNumber();
                eventNumber = data.getEventNumber();
                alarmNumber = data.getAlarmNumber();
            } else {
                alarmNumber = getAlarmHourCount(calendar, i);
            }
            receivedData[i] = receivedNumber;
            sendData[i] = sendNumber;
            eventData[i] = eventNumber;
            alarmData[i] = alarmNumber;
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("receivedData", receivedData);
        data.put("sendData", sendData);
        data.put("eventData", eventData);
        data.put("alarmData", alarmData);
        return data;
    }

    /**
     * 消息分类周统计
     * @return 返回统计结果
     */
    public JSONObject statisticsClassWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);

        long[] receivedData = new long[7];
        long[] sendData = new long[7];
        long[] eventData = new long[7];
        long[] alarmData = new long[7];
        String[] xAxis = new String[7];
        for (int i = 0; i < 7; i++) {
            int dayNum = 6 - i;

            Calendar calendar = Calendar.getInstance();
            if(dayNum > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            xAxis[i] = sdf.format(calendar.getTime());

            QueryWrapper<StatisticsOptionMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            List<StatisticsOptionMessage> list = statisticsOptionMessageMapper.selectList(example);
            long receivedNumber = 0L;
            long sendNumber = 0L;
            long eventNumber = 0L;
            long alarmNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsOptionMessage data : list) {
                    receivedNumber += data.getReceivedNumber();
                    sendNumber += data.getSendNumber();
                    eventNumber += data.getEventNumber();
                    alarmNumber += data.getAlarmNumber();
                }
            } else {
                alarmNumber = getAlarmDayCount(calendar, day);
            }
            receivedData[i] = receivedNumber;
            sendData[i] = sendNumber;
            eventData[i] = eventNumber;
            alarmData[i] = alarmNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("receivedData", receivedData);
        data.put("sendData", sendData);
        data.put("eventData", eventData);
        data.put("alarmData", alarmData);
        return data;
    }

    /**
     * 消息分类月统计
     * @return 返回统计结果
     */
    public JSONObject statisticsClassMonth() {
        Calendar now = Calendar.getInstance();
        int d = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        long[] receivedData = new long[d];
        long[] sendData = new long[d];
        long[] eventData = new long[d];
        long[] alarmData = new long[d];
        String[] xAxis = new String[d];
        for (int i = 0; i < d; i++) {
            int n = i + 1;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, n);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            xAxis[i] = n + "日";

            QueryWrapper<StatisticsOptionMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", n);
            List<StatisticsOptionMessage> list = statisticsOptionMessageMapper.selectList(example);
            long receivedNumber = 0L;
            long sendNumber = 0L;
            long eventNumber = 0L;
            long alarmNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsOptionMessage data : list) {
                    receivedNumber += data.getReceivedNumber();
                    sendNumber += data.getSendNumber();
                    eventNumber += data.getEventNumber();
                    alarmNumber += data.getAlarmNumber();
                }
            } else {
                alarmNumber = getAlarmDayCount(calendar, n);
            }
            receivedData[i] = receivedNumber;
            sendData[i] = sendNumber;
            eventData[i] = eventNumber;
            alarmData[i] = alarmNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("receivedData", receivedData);
        data.put("sendData", sendData);
        data.put("eventData", eventData);
        data.put("alarmData", alarmData);
        return data;
    }

    /**
     * 消息分类总计
     * @return 返回统计结果
     */
    public JSONArray statisticsClassTotal() {
        QueryWrapper<StatisticsOptionMessage> example = new QueryWrapper<>();
        example.select("CSUM(received_number) AS receiveds",
                "CSUM(send_number) AS sends",
                "CSUM(event_number) AS events",
                "CSUM(alarm_number) AS alarms");
        // 查询输出为map
        Map<String, Object> map = statisticsOptionMessageMapper.selectMaps(example).get(0);
        long received = Long.parseLong(map.get("receiveds").toString());
        long send = Long.parseLong(map.get("sends").toString());
        long event = Long.parseLong(map.get("events").toString());
        long alarm = Long.parseLong(map.get("alarms").toString());

        JSONArray dataArray = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("name", "接收消息");
        item.put("value", received);
        dataArray.add(item);

        item = new JSONObject();
        item.put("name", "下发消息");
        item.put("value", send);
        dataArray.add(item);

        item = new JSONObject();
        item.put("name", "事件上报");
        item.put("value", event);
        dataArray.add(item);

        item = new JSONObject();
        item.put("name", "设备告警");
        item.put("value", alarm);
        dataArray.add(item);
        return dataArray;
    }

    /**
     * 客户端连接日统计
     * @return 返回统计结果
     */
    public JSONObject statisticsConnectDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long[] connectData = new long[24];
        long[] offlineData = new long[24];
        String[] xAxis = new String[24];
        for (int i = 0; i < 24; i++) {
            xAxis[i] = i + ":00";

            QueryWrapper<StatisticsConnectMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            example.eq("hour", i);
            long count = statisticsConnectMessageMapper.selectCount(example);
            long connectNumber = 0L;
            long offlineNumber = 0L;
            if(count > 0L) {
                StatisticsConnectMessage data = statisticsConnectMessageMapper.selectOne(example);
                connectNumber = data.getConnectNumber();
                offlineNumber = data.getOfflineNumber();
            }
            connectData[i] = connectNumber;
            offlineData[i] = offlineNumber;
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("connectData", connectData);
        data.put("offlineData", offlineData);
        return data;
    }

    /**
     * 客户端连接周统计
     * @return 返回统计结果
     */
    public JSONObject statisticsConnectWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);

        long[] connectData = new long[7];
        long[] offlineData = new long[7];
        String[] xAxis = new String[7];
        for (int i = 0; i < 7; i++) {
            int dayNum = 6 - i;

            Calendar calendar = Calendar.getInstance();
            if(dayNum > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            xAxis[i] = sdf.format(calendar.getTime());

            QueryWrapper<StatisticsConnectMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            List<StatisticsConnectMessage> list = statisticsConnectMessageMapper.selectList(example);
            long connectNumber = 0L;
            long offlineNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsConnectMessage data : list) {
                    connectNumber = data.getConnectNumber();
                    offlineNumber = data.getOfflineNumber();
                }
            }
            connectData[i] = connectNumber;
            offlineData[i] = offlineNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("connectData", connectData);
        data.put("offlineData", offlineData);
        return data;
    }

    /**
     * 客户端连接月统计
     * @return 返回统计结果
     */
    public JSONObject statisticsConnectMonth() {
        Calendar now = Calendar.getInstance();
        int d = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        long[] connectData = new long[d];
        long[] offlineData = new long[d];
        String[] xAxis = new String[d];
        for (int i = 0; i < d; i++) {
            int n = i + 1;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, n);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            xAxis[i] = n + "日";

            QueryWrapper<StatisticsConnectMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", n);
            List<StatisticsConnectMessage> list = statisticsConnectMessageMapper.selectList(example);
            long connectNumber = 0L;
            long offlineNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsConnectMessage data : list) {
                    connectNumber = data.getConnectNumber();
                    offlineNumber = data.getOfflineNumber();
                }
            }
            connectData[i] = connectNumber;
            offlineData[i] = offlineNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("connectData", connectData);
        data.put("offlineData", offlineData);
        return data;
    }

    /**
     * 消息队列日统计
     * @return 返回统计结果
     */
    public JSONObject statisticsQueueDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long[] declaredData = new long[24];
        long[] createdData = new long[24];
        long[] deletedData = new long[24];
        String[] xAxis = new String[24];
        for (int i = 0; i < 24; i++) {
            xAxis[i] = i + ":00";

            QueryWrapper<StatisticsQueueMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            example.eq("hour", i);
            long count = statisticsQueueMessageMapper.selectCount(example);
            long declaredNumber = 0L;
            long createdNumber = 0L;
            long deletedNumber = 0L;
            if(count > 0L) {
                StatisticsQueueMessage data = statisticsQueueMessageMapper.selectOne(example);
                declaredNumber = data.getDeclaredNumber();
                createdNumber = data.getCreatedNumber();
                deletedNumber = data.getDeletedNumber();
            }
            declaredData[i] = declaredNumber;
            createdData[i] = createdNumber;
            deletedData[i] = deletedNumber;
        }
        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("declaredData", declaredData);
        data.put("createdData", createdData);
        data.put("deletedData", deletedData);
        return data;
    }

    /**
     * 消息队列周统计
     * @return 返回统计结果
     */
    public JSONObject statisticsQueueWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);

        long[] declaredData = new long[7];
        long[] createdData = new long[7];
        long[] deletedData = new long[7];
        String[] xAxis = new String[7];
        for (int i = 0; i < 7; i++) {
            int dayNum = 6 - i;

            Calendar calendar = Calendar.getInstance();
            if(dayNum > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            xAxis[i] = sdf.format(calendar.getTime());

            QueryWrapper<StatisticsQueueMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", day);
            List<StatisticsQueueMessage> list = statisticsQueueMessageMapper.selectList(example);
            long declaredNumber = 0L;
            long createdNumber = 0L;
            long deletedNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsQueueMessage data : list) {
                    declaredNumber = data.getDeclaredNumber();
                    createdNumber = data.getCreatedNumber();
                    deletedNumber = data.getDeletedNumber();
                }
            }
            declaredData[i] = declaredNumber;
            createdData[i] = createdNumber;
            deletedData[i] = deletedNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("declaredData", declaredData);
        data.put("createdData", createdData);
        data.put("deletedData", deletedData);
        return data;
    }

    /**
     * 消息队列月统计
     * @return 返回统计结果
     */
    public JSONObject statisticsQueueMonth() {
        Calendar now = Calendar.getInstance();
        int d = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        long[] declaredData = new long[d];
        long[] createdData = new long[d];
        long[] deletedData = new long[d];
        String[] xAxis = new String[d];
        for (int i = 0; i < d; i++) {
            int n = i + 1;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, n);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            xAxis[i] = n + "日";

            QueryWrapper<StatisticsQueueMessage> example = new QueryWrapper<>();
            example.eq("year", year);
            example.eq("month", month);
            example.eq("day", n);
            List<StatisticsQueueMessage> list = statisticsQueueMessageMapper.selectList(example);
            long declaredNumber = 0L;
            long createdNumber = 0L;
            long deletedNumber = 0L;
            if(!list.isEmpty()) {
                for (StatisticsQueueMessage data : list) {
                    declaredNumber = data.getDeclaredNumber();
                    createdNumber = data.getCreatedNumber();
                    deletedNumber = data.getDeletedNumber();
                }
            }
            declaredData[i] = declaredNumber;
            createdData[i] = createdNumber;
            deletedData[i] = deletedNumber;
        }

        JSONObject data = new JSONObject();
        data.put("xAxis", xAxis);
        data.put("declaredData", declaredData);
        data.put("createdData", createdData);
        data.put("deletedData", deletedData);
        return data;
    }
}
