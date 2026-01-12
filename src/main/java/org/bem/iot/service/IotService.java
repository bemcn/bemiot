package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.mapper.tdengine.IotDataMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.ProductModel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * IOT
 * @author jakybland
 */
@Service
public class IotService {
    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductModelMapper productModelMapper;

    @Resource
    IotDataMapper iotDataMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取实时数据
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @return 设备物模型信息
     */
    public JSONObject realTime(String deviceId, String identity) {
        JSONObject obj;
        Device device = deviceMapper.selectById(deviceId);
        if(device == null) {
            obj = parseValue(deviceId, identity, "");
            obj.put("message", "设备信息不存在");
        } else {
            String productId = device.getProductId();

            QueryWrapper<ProductModel> example = new QueryWrapper<>();
            example.eq("product_id", productId);
            example.eq("model_identity", identity);
            ProductModel model = productModelMapper.selectOne(example);
            if (model == null) {
                obj = parseValue(deviceId, identity, "");
                obj.put("message", "物模型不存在");
            } else {
                String dataType = model.getDataType();
                String key = deviceId + ":" + identity + ":value";
                String value;
                if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                    value = stringRedisTemplate.opsForValue().get(key);
                    obj = parseValue(deviceId, identity, value, dataType);
                } else {
                    obj = parseValue(deviceId, identity, dataType);
                    obj.put("message", "没有最新的数据");
                }
            }
        }
        return obj;
    }
    private JSONObject parseValue(String deviceId, String identity, String strValue, String dataType) {
        JSONObject obj = new JSONObject();
        obj.put("dataType", dataType);
        switch (dataType) {
            case "int":
            case "enum":
            case "struct":
                int intVal = Integer.parseInt(strValue);
                obj.put("value", intVal);
                break;
            case "number":
                float floatVal = Float.parseFloat(strValue);
                obj.put("value", floatVal);
                break;
            case "date":
                long timestamp = Long.parseLong(strValue);
                Date now = new Date(timestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                obj.put("value", sdf.format(now));
                break;
            case "bool":
                boolean boolVal = Boolean.parseBoolean(strValue);
                obj.put("value", boolVal);
                break;
            case "array":
                JSONArray array = JSONArray.parseArray(strValue);
                obj.put("value", array);
                break;
            default:
                obj.put("value", strValue);
                break;
        }
        obj.put("hasValue", true);
        obj.put("message", "");
        return obj;
    }
    private JSONObject parseValue(String deviceId, String identity, String dataType) {
        JSONObject obj = new JSONObject();
        obj.put("dataType", dataType);
        switch (dataType) {
            case "int":
            case "number":
            case "enum":
            case "struct":
                obj.put("value", 0);
                break;
            case "date":
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                obj.put("value", sdf.format(now));
                break;
            case "bool":
                obj.put("value", false);
                break;
            case "array":
                obj.put("value", new JSONArray());
                break;
            default:
                obj.put("value", "");
                break;
        }
        obj.put("hasValue", false);
        return obj;
    }

    /**
     * 获取单个历史模型值
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     * @param queryOut 查询方式
     * @return 物模型值
     */
    public JSONObject historicalOneValue(String deviceId, String identity, String timeFrame, String timeData, String queryOut) {
        String dataType = "";
        boolean hasValue = false;
        String value = "";
        String message = "";
        try {
            Device device = deviceMapper.selectById(deviceId);
            String productId = device.getProductId();

            QueryWrapper<ProductModel> example = new QueryWrapper<>();
            example.eq("product_id", productId);
            example.eq("model_identity", identity);
            ProductModel model = productModelMapper.selectOne(example);

            if (model == null) {
                message = "物模型不存在";
            } else {
                dataType = model.getDataType();
                QueryWrapper<Map<String, Object>> exampleIot = createExampleOne(timeFrame, timeData, queryOut);
                if (exampleIot == null) {
                    message = "查询条件错误";
                } else {
                    switch (queryOut) {
                        case "initialValue":
                            value = iotDataMapper.queryFirstValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "countValue":
                            value = iotDataMapper.queryCountBy(deviceId, exampleIot) + "";
                            dataType = "int";
                            hasValue = true;
                            break;
                        case "sumValue":
                            value = iotDataMapper.querySumValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "maxValue":
                            value = iotDataMapper.queryMaxValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "minValue":
                            value = iotDataMapper.queryMinValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "spreadValue":
                            value = iotDataMapper.querySpreadValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "averageValue":
                            value = iotDataMapper.queryAvgValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        case "stddevValue":
                            value = iotDataMapper.queryStddevValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                        default:
                            value = iotDataMapper.queryLastValue(deviceId, identity, exampleIot).toString();
                            hasValue = true;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            message = "查询异常";
        }

        JSONObject obj = new JSONObject();
        obj.put("dataType", dataType);
        obj.put("value", value);
        obj.put("hasValue", hasValue);
        obj.put("message", message);
        return obj;
    }
    private QueryWrapper<Map<String, Object>> createExampleOne(String timeFrame, String timeData, String queryOut) {
        try {
            QueryWrapper<Map<String, Object>> example = new QueryWrapper<>();
            Long[] timeArray = switch (timeFrame) {
                case "min" ->  //分钟
                        minBetweenArray(timeData);
                case "hour" ->  //小时
                        hourBetweenArray(timeData);
                case "day" ->  //天
                        dayBetweenArray(timeData);
                case "betweenTime" ->  //时间期间
                        timeBetweenArray(timeData);
                case "betweenDate" ->  //日期期间
                        dateBetweenArray(timeData);
                case "betweenDateTime" ->  //日期时间期间
                        dateTimeBetweenArray(timeData);
                default ->  //当前（时间到上一整点）
                        nowBetweenArray();
            };
            example.between("ts", timeArray[0], timeArray[1]);

            if ("initialValue".equals(queryOut)) {
                example.orderByAsc("ts");
            } else if ("latestValue".equals(queryOut)) {
                example.orderByDesc("ts");
            }

            return example;
        } catch (Exception ignored) {
            return null;
        }
    }
    private QueryWrapper<Map<String, Object>> createExample(String timeFrame, String timeData, String orderBy) {
        try {
            QueryWrapper<Map<String, Object>> example = new QueryWrapper<>();
            Long[] timeArray = switch (timeFrame) {
                case "min" ->  //分钟
                        minBetweenArray(timeData);
                case "hour" ->  //小时
                        hourBetweenArray(timeData);
                case "day" ->  //天
                        dayBetweenArray(timeData);
                case "betweenTime" ->  //时间期间
                        timeBetweenArray(timeData);
                case "betweenDate" ->  //日期期间
                        dateBetweenArray(timeData);
                case "betweenDateTime" ->  //日期时间期间
                        dateTimeBetweenArray(timeData);
                default ->  //当前（时间到上一整点）
                        nowBetweenArray();
            };
            example.between("ts", timeArray[0], timeArray[1]);

            if(StrUtil.isNotEmpty(orderBy)) {
                if ("asc".equals(orderBy)) {
                    example.orderByAsc("ts");
                } else {
                    example.orderByDesc("ts");
                }
            }

            return example;
        } catch (Exception ignored) {
            return null;
        }
    }
    private Long[] nowBetweenArray() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        
        return getTimeRange(startOfDay, now);
    }
    private Long[] minBetweenArray(String timeData) {
        int m = Integer.parseInt(timeData);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = now.minusMinutes(m).withSecond(0).withNano(0);
        
        return getTimeRange(startDateTime, now);
    }
    private Long[] hourBetweenArray(String timeData) {
        int h = Integer.parseInt(timeData);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = now.minusHours(h).withMinute(0).withSecond(0).withNano(0);
        
        return getTimeRange(startDateTime, now);
    }
    private Long[] dayBetweenArray(String timeData) {
        int d = Integer.parseInt(timeData);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = now.minusDays(d).truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        
        return getTimeRange(startDateTime, now);
    }
    private Long[] getTimeRange(LocalDateTime start, LocalDateTime end) {
        long startTimer = start.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimer = end.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        return new Long[]{startTimer, endTimer};
    }
    private Long[] timeBetweenArray(String timeData) {
        String[] timeDataArray = timeData.split(",");
        String startTimeStr = timeDataArray[0];
        String endTimeStr = timeDataArray[1];
        
        LocalDateTime now = LocalDateTime.now();
        java.time.LocalTime startTime = java.time.LocalTime.parse(startTimeStr);
        java.time.LocalTime endTime = java.time.LocalTime.parse(endTimeStr);
        
        LocalDateTime startDateTime = now.with(startTime);
        LocalDateTime endDateTime = now.with(endTime);
        
        return getTimeRange(startDateTime, endDateTime);
    }
    private Long[] dateBetweenArray(String dateData) {
        String[] timeDataArray = dateData.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime startDateTime = LocalDateTime.parse(timeDataArray[0], formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(timeDataArray[1], formatter);
        return getTimeRange(startDateTime, endDateTime);
    }
    private Long[] dateTimeBetweenArray(String dateData) {
        String[] timeDataArray = dateData.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startDateTime = LocalDateTime.parse(timeDataArray[0], formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(timeDataArray[1], formatter);
        return getTimeRange(startDateTime, endDateTime);
    }

    /**
     * 获取单条设备历史数据
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     * @return 设备物模型信息
     */
    public Map<String, Object> historicalFind(String deviceId, String identity, String timeFrame, String timeData) {
        Map<String, Object> data = null;
        try {
            Device device = deviceMapper.selectById(deviceId);
            String productId = device.getProductId();

            QueryWrapper<ProductModel> example = new QueryWrapper<>();
            example.eq("product_id", productId);
            example.eq("model_identity", identity);
            ProductModel model = productModelMapper.selectOne(example);

            if (model != null) {
                QueryWrapper<Map<String, Object>> exampleIot = createExample(timeFrame, timeData, "");
                if (exampleIot != null) {
                    data = iotDataMapper.queryLast(deviceId, exampleIot);
                }
            }
        } catch (Exception ignored) {
        }
        return data;
    }


    /**
     * 获取历史数据
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     * @param orderBy 排序
     * @return 设备物模型信息
     */
    public IPage<Map<String, Object>> historicalPage(String deviceId, String identity, String timeFrame, String timeData, String orderBy, int index, int size) {
        IPage<Map<String, Object>> pageData = null;
        try {
            Page<Map<String, Object>> page = new Page<>(index, size);

            Device device = deviceMapper.selectById(deviceId);
            String productId = device.getProductId();

            QueryWrapper<ProductModel> example = new QueryWrapper<>();
            example.eq("product_id", productId);
            example.eq("model_identity", identity);
            ProductModel model = productModelMapper.selectOne(example);

            if (model != null) {
                QueryWrapper<Map<String, Object>> exampleIot = createExample(timeFrame, timeData, orderBy);
                if (exampleIot != null) {
                    pageData = iotDataMapper.queryListPage(deviceId, page, exampleIot);
                }
            }
        } catch (Exception ignored) {
        }
        return pageData;
    }
}
