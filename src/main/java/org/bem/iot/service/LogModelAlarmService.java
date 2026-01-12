package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.mapper.tdengine.LogModelAlarmMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.log.LogModelAlarm;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 物模型操作日志
 * @author jakybland
 */
@Service
public class LogModelAlarmService {
    @Resource
    LogModelAlarmMapper logModelAlarmMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductModelMapper productModelMapper;

    /**
     * 获取统计数量
     * @param example 查询条件
     * @return 返回数量值
     */
    public long count(QueryWrapper<LogModelAlarm> example) {
        return logModelAlarmMapper.selectCount(example);
    }

    /**
     * 获取等级统计
     * @return 返回数量值
     */
    public JSONObject countByLevel() {
        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.eq("alarm_status", 1);
        long total = logModelAlarmMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("level", 1);
        example.eq("alarm_status", 1);
        long minor = logModelAlarmMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("level", 2);
        example.eq("alarm_status", 1);
        long major = logModelAlarmMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("level", 3);
        example.eq("alarm_status", 1);
        long critical = logModelAlarmMapper.selectCount(example);

        JSONObject obj = new JSONObject();
        obj.put("total", total);
        obj.put("minor", minor);
        obj.put("major", major);
        obj.put("critical", critical);
        return obj;
    }

    /**
     * 获取近7日统计
     * @return 返回数量值
     */
    public List<Map<String, Object>> countBySevenDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long endOfDay = calendar.getTimeInMillis();
        long startOfDay = endOfDay - 6 * 24 * 60 * 60 * 1000L;

        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            long dayStart = startOfDay + i * 24 * 60 * 60 * 1000L;
            long dayEnd = dayStart + 24 * 60 * 60 * 1000L;
            String dateName = Instant.ofEpochMilli(dayStart).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
            example.between("ts", dayStart, dayEnd);
            example.eq("level", 1);
            example.eq("alarm_status", 1);
            long minor = logModelAlarmMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", dayStart, dayEnd);
            example.eq("level", 2);
            example.eq("alarm_status", 1);
            long major = logModelAlarmMapper.selectCount(example);

            example = new QueryWrapper<>();
            example.between("ts", dayStart, dayEnd);
            example.eq("level", 3);
            example.eq("alarm_status", 1);
            long critical = logModelAlarmMapper.selectCount(example);

            Map<String, Object> item = new HashMap<>();
            item.put("date", dateName);
            item.put("minor", minor);
            item.put("major", major);
            item.put("critical", critical);
            list.add(item);
        }
        return list;
    }

    /**
     * 获取设备告警排行
     * @param size 数量
     * @return 返回数量值
     */
    public List<Map<String, Object>> countByDeviceRanking(int size) {
        long count = logModelAlarmMapper.selectCount(new QueryWrapper<>());
        List<Map<String, Object>> list = new ArrayList<>();
        if(count > 0) {
            // 按照设备ID分组进行数量统计，按统计数量进行排行
            List<Map<String, Object>> totalList = logModelAlarmMapper.queryGroupByDevice(size);
            for (Map<String, Object> map : totalList) {
                String deviceId = map.get("device_id").toString();
                long total = Long.parseLong(map.get("total").toString());

                Device device = deviceMapper.selectById(deviceId);
                String deviceName = device.getDeviceName();

                Map<String, Object> item = new HashMap<>();
                item.put("name", deviceName);
                item.put("value", total);
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 查询指定数量的数据
     * @param example 查询条件
     * @param size 显示数量
     * @return 符合条件的数据列表
     */
    public List<LogModelAlarm> selectLimit(QueryWrapper<LogModelAlarm> example, long size) {
        IPage<LogModelAlarm> page = new Page<>(1L, size);
        IPage<LogModelAlarm> result = logModelAlarmMapper.selectPage(page, example);
        List<LogModelAlarm> list = result.getRecords();
        for (LogModelAlarm alarm : list) {
            String deviceId = alarm.getDeviceId();
            String productId = alarm.getProductId();
            String modelIdentity = alarm.getModelIdentity();

            Device device = deviceMapper.selectById(deviceId);
            Product product = productMapper.selectById(productId);

            QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
            exampleModel.eq("product_id", productId);
            exampleModel.eq("model_identity", modelIdentity);
            ProductModel model = productModelMapper.selectOne(exampleModel);

            alarm.setDevice(device);
            alarm.setProduct(product);
            alarm.setModel(model);
        }
        return list;
    }

    /**
     * 分页查询数据
     * @param example 查询条件
     * @param index 查询的页码
     * @param size 每页数量
     * @return 返回查询结果
     */
    public IPage<LogModelAlarm> selectPage(QueryWrapper<LogModelAlarm> example, long index, long size) {
        Page<LogModelAlarm> page = new Page<>(index, size);
        IPage<LogModelAlarm> result = logModelAlarmMapper.selectPage(page, example);
        List<LogModelAlarm> list = result.getRecords();
        for (LogModelAlarm alarm : list) {
            String deviceId = alarm.getDeviceId();
            String productId = alarm.getProductId();
            String modelIdentity = alarm.getModelIdentity();

            Device device = deviceMapper.selectById(deviceId);
            Product product = productMapper.selectById(productId);

            QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
            exampleModel.eq("product_id", productId);
            exampleModel.eq("model_identity", modelIdentity);
            ProductModel model = productModelMapper.selectOne(exampleModel);

            alarm.setDevice(device);
            alarm.setProduct(product);
            alarm.setModel(model);
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 插入新的数据
     * @param record 新数据
     */
    public void insert(LogModelAlarm record) {
        logModelAlarmMapper.insert(record);
    }

    /**
     * 删除数据
     * @param id 删除的ID
     */
    public void del(@Param("id") String id) {
        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.eq("id", id);
        logModelAlarmMapper.delete(example);
    }

    /**
     * 批量删除数据
     * @param idList 删除的ID列表
     */
    public void delArray(List<String> idList) {
        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        example.in("id", idList);
        logModelAlarmMapper.delete(example);
    }
}
