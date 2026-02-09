package org.bem.iot.mqtt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.mapper.tdengine.IotDataMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.general.*;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.mqtt.config.MqttConfig;
import org.bem.iot.util.AmapUitl;
import org.bem.iot.util.ConvertUtil;
import org.bem.iot.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * MQTT消息处理服务接口
 */
@Service
public class MqttMessageService {
    private static final Logger logger = LoggerFactory.getLogger(MqttMessageService.class);

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    DriveMapper driveMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductModelMapper productModelMapper;

    @Resource
    FirmwareVersionMapper firmwareVersionMapper;

    @Resource
    FirmwareUpdateTaskMapper firmwareUpdateTaskMapper;

    @Resource
    FirmwareUpdateLogMapper firmwareUpdateLogMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    IotDataMapper iotDataMapper;

    @Value("${map-key}")
    String mapKey;

    /**
     * 处理驱动消息
     * 本端（与设备端相反）：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/register/{deviceCode}/post  接收驱动注册申请 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/heartbeat/{deviceCode}/post  接收驱动心跳包 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/statistics/{deviceCode}/post  接收驱动统计数据 <br/>
     * 返回状态值：200：成功， 400：驱动未注册 500：消息提交错误
     * @param topic 主题
     * @param payload 消息内容
     */
    public void handleDriveMessage(String topic, String payload) {
        logger.info("处理驱动消息, 主题: {}, 内容: {}", topic, payload);
        String[] topicArr = topic.split("/");
        if (topicArr.length < 4) {
            logger.error("无效的主题格式: {}", topic);
            return;
        }
        String target = topicArr[1];
        String driveCode = topicArr[2];

        // 是否注册指令
        if("register".equals(target)) {
            driveRegister(driveCode, payload);
        } else if ("heartbeat".equals(target)) {
            driveHeartbeat(driveCode);
        } else if ("statistics".equals(target)) {
            driveStatistics(driveCode, payload);
        }
    }
    private void driveRegister(String driveCode, String payload) {
        JSONObject result = new JSONObject();
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        if(!driveMapper.exists(example)) {
            try {
                JSONObject regObj = JSONObject.parseObject(payload);
                String releaseTime = regObj.getString("releaseTime");
                Date releaseTimeDate;
                if (StrUtil.isEmpty(releaseTime)) {
                    releaseTimeDate = new Date();
                } else {
                    try {
                        releaseTimeDate = DateUtil.parse(releaseTime);
                    } catch (Exception e) {
                        releaseTimeDate = new Date();
                    }
                }

                Drive drive = new Drive();
                drive.setDriveCode(driveCode);
                drive.setDriveName(regObj.getString("driveName"));
                drive.setProtocolId(1);
                drive.setProtocolName(regObj.getString("protocolName"));
                drive.setDriveSource(regObj.getString("driveSource"));
                drive.setVersion(regObj.getString("version"));
                drive.setPackageUrl(regObj.getString("packageUrl"));
                drive.setRemark(regObj.getString("remark"));
                drive.setStatus(1);
                drive.setDefaultDrive(0);
                drive.setReleaseTime(releaseTimeDate);
                driveMapper.insert(drive);

                result.put("code", 200);
            } catch (Exception e) {
                logger.error("驱动注册数据错误,驱动编号：{}，错误数据：{}, 错误描述：{}", driveCode, payload, e.getMessage());
                result.put("code", 500);
            }
        } else {
            result.put("code", 200);
        }
        publishDataMessage("/drive/register/" + driveCode + "/get", result, 2);
    }
    private void driveHeartbeat(String driveCode) {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        example.eq("status", 1);
        boolean exists = driveMapper.exists(example);
        if(exists) {
            Drive record = driveMapper.selectById(driveCode);
            record.setStatus(2);
            driveMapper.updateById(record);
        }
    }
    private void driveStatistics(String driveCode, String payload) {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        if(driveMapper.exists(example)) {
            JSONObject msgObj = JSONObject.parseObject(payload);
            if (msgObj.containsKey("sendNumber") || msgObj.containsKey("receiveNumber")) {
                Object sendNumberObj = msgObj.get("sendNumber");
                Object receiveNumberObj = msgObj.get("receiveNumber");

                boolean isSendByInt = NumberUtil.isInteger(sendNumberObj.toString());
                boolean isReceiveByInt = NumberUtil.isInteger(receiveNumberObj.toString());
                if (isSendByInt && isReceiveByInt) {
                    String key = "drive:statistics:" + driveCode;
                    stringRedisTemplate.opsForValue().set(key, payload);
                }
            }
        }
    }

    /**
     * 处理设备消息<br/>
     * 本端（与设备端相反）：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/active/{deviceCode}/post  接收设备激活申请 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/heartbeat/{deviceCode}/post  接收设备心跳包 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/monitor/{deviceCode}/post  接收实时监测数据 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/property/{deviceCode}/post  接收监测属性数据 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/info/{deviceCode}/post  接收设备信息 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/ntp/{deviceCode}/post  接收时钟同步申请 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/http/{deviceCode}/reply  接收HTTP方式回复OTA升级响应 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/fetch/{deviceCode}/reply  接收二进制包方式回复OTA升级响应 <br/>
     * 返回状态值：200：成功， 220：已是最新版本，无需升级 400：设备不存在 405：设备未激活 500：消息提交错误
     * @param topic 主题
     * @param payload 消息内容
     */
    public void handleDeviceMessage(String topic, String payload) {
        logger.info("处理设备消息, 主题: {}, 内容: {}", topic, payload);
        String[] topicArr = topic.split("/");
        if (topicArr.length < 4) {
            logger.error("无效的主题格式: {}", topic);
            return;
        }
        String target = topicArr[1];
        String deviceCode = topicArr[2];

        if("active".equals(target)) {
            deviceActive(deviceCode, payload);
        } else if("heartbeat".equals(target)) {
            deviceHeartbeat(deviceCode);
        } else if("monitor".equals(target)) {
            deviceMonitor(deviceCode, payload);
        } else if("property".equals(target)) {
            deviceProperty(deviceCode, payload);
        } else if("info".equals(target)) {
            deviceInfo(deviceCode, payload);
        } else if("ntp".equals(target)) {
            deviceNtpTime(deviceCode);
        } else if("http".equals(target)) {
            deviceOtaHttp(deviceCode, payload);
        } else if("fetch".equals(target)) {
            deviceOtaFetch(deviceCode, payload);
        }
    }

    /**
     * 设备激活申请<br/>
     * 设备主动上报
     * @param deviceCode 设备编号
     * @param payload 激活数据
     */
    private void deviceActive(String deviceCode, String payload) {
        JSONObject result = new JSONObject();

        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 3);
        if(deviceMapper.exists(example)) {
            result.put("code", 200);
        } else {
            example = new QueryWrapper<>();
            example.eq("device_id", deviceCode);
            example.ne("types", 5);
            example.eq("status", 1);
            if(deviceMapper.exists(example)) {
                Device device = deviceMapper.selectOne(example);
                JSONObject regObj = null;
                try {
                    regObj = JSONObject.parseObject(payload);
                } catch (Exception e) {
                    logger.error("设备激活数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
                }
                if (regObj != null) {
                    if (regObj.containsKey("firmwareVersion")) {
                        String firmwareVersion = regObj.getString("firmwareVersion");
                        firmwareVersion = firmwareVersion.replace("v", "");
                        device.setFirmwareVersion(firmwareVersion);
                    }
                    if (device.getLocateMethod() == 1) {
                        if (regObj.containsKey("longitude") && regObj.containsKey("latitude")) {
                            BigDecimal longitude = new BigDecimal(regObj.getString("longitude"));
                            BigDecimal latitude = new BigDecimal(regObj.getString("latitude"));
                            device.setLongitude(longitude);
                            device.setLatitude(latitude);
                            // 根据定位更新设备位置
                            String location = longitude + "," + latitude;
                            try {
                                String address = AmapUitl.getGeoAddress(location, mapKey);
                                device.setAddress(address);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    device.setStatus(3);
                    device.setActiveTime(new Date());
                    deviceMapper.updateById(device);

                    result.put("code", 200);
                } else {
                    result.put("code", 500);
                }
            } else {
                result.put("code", 400);
            }
        }

        String key = "device_online:" + deviceCode;
        stringRedisTemplate.opsForValue().set(key, "2");

        publishDataMessage("/device/active/" + deviceCode + "/get", result, 2);
    }

    /**
     * 设备心跳包<br/>
     * 设备主动上报
     * @param deviceCode 设备编号
     */
    private void deviceHeartbeat(String deviceCode) {
        String key = "device_online:" + deviceCode;
        stringRedisTemplate.opsForValue().set(key, "2");
    }

    /**
     * 设备物模型属性实时上报<br/>
     * 平台发起请求，设备实时上报
     * @param deviceCode 设备编号
     * @param payload 属性数据
     */
    private void deviceMonitor(String deviceCode, String payload) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 3);
        if(deviceMapper.exists(example)) {
            JSONObject regObj = null;
            try {
                regObj = JSONObject.parseObject(payload);
            } catch (Exception e) {
                logger.error("接收设备物模型属性数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
            }
            if (regObj != null) {
                Device device = deviceMapper.selectOne(example);
                String productId = device.getProductId();

                QueryWrapper<ProductModel> productExample = new QueryWrapper<>();
                productExample.eq("product_id", productId);
                productExample.eq("model_type", 0);
                productExample.eq("model_class", 1);
                productExample.orderByAsc("create_time");
                List<ProductModel> modelList = productModelMapper.selectList(productExample);
                saveMonitor(deviceCode, modelList, regObj);
            }
        }
        String key = "device_online:" + deviceCode;
        stringRedisTemplate.opsForValue().set(key, "2");
    }
    private void saveMonitor(String deviceCode, List<ProductModel> modelList, JSONObject regObj) {
        for (ProductModel model : modelList) {
            String identity = model.getModelIdentity();
            String dataType = model.getDataType();
            String dataDefinition = model.getDataDefinition();
            JSONObject definition = JSONObject.parseObject(dataDefinition);

            String key = deviceCode + ":" + identity + ":value";
            String cacheValue = "null";
            if (regObj.containsKey(identity)) {
                String objValue = regObj.getString(identity);
                if (StrUtil.isEmpty(objValue)) {
                    if ("bool".equals(dataType)) {
                        cacheValue = "false";
                    }
                } else {
                    if ("int".equals(dataType)) {
                        int intValue = -99999999;
                        try {
                            intValue = Integer.parseInt(objValue);
                            cacheValue = intValue + "";
                        } catch (Exception ignored) {
                        }
                    } else if ("number".equals(dataType)) {
                        float floatValue = ToolUtil.getValidValue(objValue);
                        if (floatValue != -99999999.0f) {
                            cacheValue = String.valueOf(floatValue);
                        }
                    } else if ("text".equals(dataType)) {
                        String maxLengthStr = definition.getString("maxLength");
                        int maxLength = Integer.parseInt(maxLengthStr);
                        if(objValue.length() > maxLength) {
                            cacheValue = objValue.substring(0, maxLength);
                        } else {
                            cacheValue = objValue;
                        }
                    } else if ("timestamp".equals(dataType)) {
                        long timeValue = System.currentTimeMillis();
                        try {
                            if(objValue.length() == 10) {
                                objValue = objValue + "000";
                            }
                            if(objValue.length() == 13) {
                                timeValue = Long.parseLong(objValue);
                                cacheValue = timeValue + "";
                            }
                        } catch (Exception ignored) {
                        }
                    } else if ("bool".equals(dataType)) {
                        boolean boolValue = false;
                        try {
                            boolValue = Boolean.parseBoolean(objValue);
                            cacheValue = boolValue + "";
                        } catch (Exception ignored) {
                        }
                    } else if ("array".equals(dataType)) {
                        String[] arrayValue = objValue.split(",");
                        String lengthStr = definition.getString("length");
                        int length = Integer.parseInt(lengthStr);
                        if(arrayValue.length == length) {
                            cacheValue = objValue;
                        }
                    } else if ("enum".equals(dataType)) {
                        int enumValue = 0;
                        try {
                            enumValue = Integer.parseInt(objValue);
                            cacheValue = enumValue + "";
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else {
                cacheValue = "null";
                if ("bool".equals(dataType)) {
                    cacheValue = "false";
                }
            }
            stringRedisTemplate.opsForValue().set(key, cacheValue);
        }
    }

    /**
     * 设备物模型属性上报<br/>
     * 设备主动上报
     * @param deviceCode 设备ID
     * @param payload 属性数据
     */
    private void deviceProperty(String deviceCode, String payload) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 3);
        if(deviceMapper.exists(example)) {
            JSONObject regObj = null;
            try {
                regObj = JSONObject.parseObject(payload);
            } catch (Exception e) {
                logger.error("接收设备物模型属性数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
            }
            if (regObj != null) {
                Device device = deviceMapper.selectOne(example);
                String productId = device.getProductId();

                QueryWrapper<ProductModel> productExample = new QueryWrapper<>();
                productExample.eq("product_id", productId);
                productExample.eq("model_type", 0);
                productExample.eq("model_class", 1);
                productExample.orderByAsc("create_time");
                List<ProductModel> modelList = productModelMapper.selectList(productExample);
                saveProperty(deviceCode, modelList, regObj);
            }
        }
        String key = "device_online:" + deviceCode;
        stringRedisTemplate.opsForValue().set(key, "2");
    }
    private void saveProperty(String deviceCode, List<ProductModel> modelList, JSONObject regObj) {
        long timestamp = System.currentTimeMillis();
        // 验证deviceCode格式，防止SQL注入
        if (!deviceCode.matches("^[a-zA-Z0-9_-]+$")) {
            logger.error("Invalid device code format: {}", deviceCode);
            return;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO bemcn." + deviceCode + " VALUES (" + timestamp);
        for (ProductModel model : modelList) {
            String identity = model.getModelIdentity();
            String dataType = model.getDataType();
            String dataDefinition = model.getDataDefinition();
            JSONObject definition = JSONObject.parseObject(dataDefinition);

            String key = deviceCode + ":" + identity + ":value";
            String cacheValue = "null";
            if (regObj.containsKey(identity)) {
                String objValue = regObj.getString(identity);
                if (StrUtil.isEmpty(objValue)) {
                    if ("bool".equals(dataType)) {
                        cacheValue = "false";
                    }
                    sql.append(",").append(cacheValue);
                } else {
                    if ("int".equals(dataType)) {
                        int intValue = -99999999;
                        try {
                            intValue = Integer.parseInt(objValue);
                            cacheValue = intValue + "";
                        } catch (Exception ignored) {
                        }
                        sql.append(",").append(intValue);
                    } else if ("number".equals(dataType)) {
                        float floatValue = ToolUtil.getValidValue(objValue);
                        if (floatValue != -99999999.0f) {
                            cacheValue = String.valueOf(floatValue);
                        }
                        sql.append(",").append(cacheValue);
                    } else if ("text".equals(dataType)) {
                        String maxLengthStr = definition.getString("maxLength");
                        int maxLength = Integer.parseInt(maxLengthStr);
                        if(objValue.length() > maxLength) {
                            cacheValue = objValue.substring(0, maxLength);
                        } else {
                            cacheValue = objValue;
                        }
                        sql.append(",'").append(cacheValue).append("'");
                    } else if ("timestamp".equals(dataType)) {
                        long timeValue = System.currentTimeMillis();
                        try {
                            if(objValue.length() == 10) {
                                objValue = objValue + "000";
                            }
                            if(objValue.length() == 13) {
                                timeValue = Long.parseLong(objValue);
                                cacheValue = timeValue + "";
                            }
                            sql.append(",'").append(timeValue).append("'");
                        } catch (Exception ignored) {
                        }
                        sql.append(",").append(timeValue);
                    } else if ("bool".equals(dataType)) {
                        boolean boolValue = false;
                        try {
                            boolValue = Boolean.parseBoolean(objValue);
                            cacheValue = boolValue + "";
                        } catch (Exception ignored) {
                        }
                        sql.append(",").append(cacheValue);
                    } else if ("array".equals(dataType)) {
                        String[] arrayValue = objValue.split(",");
                        String lengthStr = definition.getString("length");
                        int length = Integer.parseInt(lengthStr);
                        if(arrayValue.length == length) {
                            cacheValue = objValue;
                        }
                        sql.append(",").append(cacheValue);
                    } else if ("enum".equals(dataType)) {
                        int enumValue = 0;
                        try {
                            enumValue = Integer.parseInt(objValue);
                            cacheValue = enumValue + "";
                        } catch (Exception ignored) {
                        }
                        sql.append(",").append(cacheValue);
                    } else {
                        sql.append(",").append(cacheValue);
                    }
                }
            } else {
                cacheValue = "null";
                if ("bool".equals(dataType)) {
                    cacheValue = "false";
                }
            }
            stringRedisTemplate.opsForValue().set(key, cacheValue);
        }
        sql.append(")");

        // 执行SQL语句
        try {
            iotDataMapper.executeSql(sql.toString());
            logger.info("数据插入成功: {}", sql.toString());
        } catch (Exception e) {
            logger.error("数据插入失败: {}, 错误: {}", sql.toString(), e.getMessage());
        }

    }

    /**
     * 设备信息上报<br/>
     * 平台发起请求，设备实时上报
     * @param deviceCode 设备ID
     * @param payload 设备信息数据
     */
    private void deviceInfo(String deviceCode, String payload) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 1);
        if(deviceMapper.exists(example)) {
            JSONObject regObj = new JSONObject();
            try {
                regObj = JSONObject.parseObject(payload);
            } catch (Exception e) {
                logger.error("设备信息数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
            }
            if (regObj != null) {
                Device device = deviceMapper.selectOne(example);
                if (regObj.containsKey("firmwareVersion")) {
                    String firmwareVersion = regObj.getString("firmwareVersion");
                    firmwareVersion = firmwareVersion.replace("v", "");
                    device.setFirmwareVersion(firmwareVersion);
                }
                if (device.getLocateMethod() == 1) {
                    if (regObj.containsKey("longitude") && regObj.containsKey("latitude")) {
                        BigDecimal longitude = new BigDecimal(regObj.getString("longitude"));
                        BigDecimal latitude = new BigDecimal(regObj.getString("latitude"));
                        device.setLongitude(longitude);
                        device.setLatitude(latitude);
                        // 根据定位更新设备位置
                        String location = longitude + "," + latitude;
                        try {
                            String address = AmapUitl.getGeoAddress(location, mapKey);
                            device.setAddress(address);
                        } catch (Exception ignored) {
                        }
                    }
                }
                deviceMapper.updateById(device);
            }
        }
    }

    /**
     * 设备NTP时间同步<br/>
     * 设备发起请求，平台返回NTP时间
     * @param deviceCode 设备ID
     */
    private void deviceNtpTime(String deviceCode) {
        JSONObject result = new JSONObject();
        long timestamp = System.currentTimeMillis();
        result.put("timestamp", timestamp);
        publishDataMessage("device/" + deviceCode + "/ntp", result, 2);
    }

    /**
     * 设备OTA升级请求（HTTP）<br/>
     * 设备发起请求，平台返回OTA升级信息
     * @param deviceCode 设备ID
     * @param payload 设备信息数据
     */
    private void deviceOtaHttp(String deviceCode, String payload) {
        JSONObject result = new JSONObject();

        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 1);
        if(deviceMapper.exists(example)) {
            JSONObject regObj = new JSONObject();
            try {
                regObj = JSONObject.parseObject(payload);
            } catch (Exception e) {
                logger.error("设备信息数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
            }
            if (regObj != null) {
                if (regObj.containsKey("version") && regObj.containsKey("status")) {
                    String type = regObj.getString("type");
                    if("update".equals(type)) {
                        result = otaFirmwareHttp(example, type, regObj);
                    } else if("updateTask".equals(type)) {
                        result = otaFirmwareHttpTask(example, type, deviceCode, regObj);
                    } else if("taskStatus".equals(type)) {
                        result = otaFirmwareVersionTaskStatus(type, deviceCode, regObj);
                    } else {
                        result.put("code", 500);
                        result.put("type", type);
                    }
                } else {
                    result.put("code", 500);
                }
            } else {
                result.put("code", 500);
            }
        } else {
            result.put("code", 400);
        }
        publishDataMessage("/device/http/" + deviceCode + "/set", result, 2);
    }
    private JSONObject otaFirmwareHttp(QueryWrapper<Device> example, String type, JSONObject regObj) {
        String version = regObj.getString("version");
        return httpOtaFirmwareVersion(example, type, version, 0, "");
    }
    private JSONObject otaFirmwareHttpTask(QueryWrapper<Device> example, String type, String deviceCode, JSONObject regObj) {
        String version = regObj.getString("version");
        int taskId = 0;
        String taskName = "";
        if(regObj.containsKey("taskId")) {
            try {
                int reqTaskId = Integer.parseInt(regObj.getString("taskId"));
                FirmwareUpdateTask task = firmwareUpdateTaskMapper.selectById(reqTaskId);
                int taskType = task.getTaskType();
                if(taskType == 1) {
                    taskId = reqTaskId;
                    taskName = task.getTaskName();
                } else {
                    taskId = -2;
                    JSONArray devArray = JSONArray.parseArray(task.getDevices());
                    for (int i = 0; i < devArray.size(); i++) {
                        JSONObject devObj = devArray.getJSONObject(i);
                        if(deviceCode.equals(devObj.getString("deviceId"))) {
                            taskId = reqTaskId;
                            taskName = task.getTaskName();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                taskId = -1;
            }
        }
        if (taskId > 0) {
            return httpOtaFirmwareVersion(example, type, version, taskId, taskName);
        } else {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("type", type);
            return result;
        }
    }
    private JSONObject httpOtaFirmwareVersion(QueryWrapper<Device> example, String type, String version, int taskId, String taskName) {
        Device device = deviceMapper.selectOne(example);
        String ProductId = device.getProductId();
        Product product = productMapper.selectById(ProductId);
        int firmwareId = product.getFirmwareId();

        QueryWrapper<FirmwareVersion> versionExample = new QueryWrapper<>();
        versionExample.eq("firmware_id", firmwareId);
        versionExample.orderByDesc("release_time");
        FirmwareVersion firmwareVersion = firmwareVersionMapper.selectOne(versionExample);
        String lastVersion = firmwareVersion.getVersion();
        String url = firmwareVersion.getUrl();

        boolean isUpdate = ConvertUtil.versionNotCompare(lastVersion, version);
        JSONObject result = new JSONObject();
        if(isUpdate) {
            result.put("code", 200);
            result.put("type", type);
            result.put("url", url);
            result.put("newVersion", lastVersion);

            if(taskId > 0) {
                String deviceId = device.getDeviceId();
                String deviceName = device.getDeviceName();

                FirmwareUpdateLog log = new FirmwareUpdateLog();
                log.setFirmwareId(firmwareId);
                log.setTaskId(taskId);
                log.setTaskName(taskName);
                log.setDeviceId(deviceId);
                log.setDeviceName(deviceName);
                log.setVersion(lastVersion);
                log.setStatus(2);
                log.setUpdateTime(new Date());
                firmwareUpdateLogMapper.insert(log);
                long logId = log.getLogId();
                result.put("taskDataId", logId);
            }
        } else {
            result.put("code", 220);
            result.put("type", type);
            result.put("newVersion", lastVersion);
        }
        return result;
    }

    /**
     * 设备OTA升级请求（分包）<br/>
     * 设备发起请求，平台返回OTA升级信息
     * @param deviceCode 设备ID
     * @param payload 设备信息数据
     */
    private void deviceOtaFetch(String deviceCode, String payload) {
        JSONObject result = new JSONObject();

        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("device_id", deviceCode);
        example.ne("types", 5);
        example.eq("status", 1);
        if(deviceMapper.exists(example)) {
            JSONObject regObj = new JSONObject();
            try {
                regObj = JSONObject.parseObject(payload);
            } catch (Exception e) {
                logger.error("设备信息数据错误,设备ID：{}，错误数据：{}, 错误描述：{}", deviceCode, payload, e.getMessage());
            }
            if (regObj != null) {
                if (regObj.containsKey("version") && regObj.containsKey("status")) {
                    String type = regObj.getString("type");
                    if("updateFetch".equals(type)) {
                        result = otaFirmwareFetch(example, type, deviceCode, regObj);
                    } else if("updateTaskFetch".equals(type)) {
                        result = otaFirmwareFetchTask(example, type, deviceCode, regObj);
                    } else if("sendFetch".equals(type)) {
                        result = otaSendFetchData(example, type, regObj);
                    } else if("taskStatus".equals(type)) {
                        result = otaFirmwareVersionTaskStatus(type, deviceCode, regObj);
                    } else {
                        result.put("code", 500);
                        result.put("type", type);
                    }
                } else {
                    result.put("code", 500);
                }
            } else {
                result.put("code", 500);
            }
        } else {
            result.put("code", 400);
        }
        publishDataMessage("/device/http/" + deviceCode + "/set", result, 2);
    }
    private JSONObject otaFirmwareFetch(QueryWrapper<Device> example, String deviceCode, String type, JSONObject regObj) {
        String version = regObj.getString("version");
        return getOtaFirmwareFetch(example, deviceCode, type, version, 0, "");
    }
    private JSONObject otaFirmwareFetchTask(QueryWrapper<Device> example, String deviceCode, String type, JSONObject regObj) {
        String version = regObj.getString("version");
        int taskId = 0;
        String taskName = "";
        JSONObject result = new JSONObject();
        if(regObj.containsKey("taskId")) {
            try {
                int reqTaskId = Integer.parseInt(regObj.getString("taskId"));
                FirmwareUpdateTask task = firmwareUpdateTaskMapper.selectById(reqTaskId);
                int taskType = task.getTaskType();
                if(taskType == 1) {
                    taskId = reqTaskId;
                    taskName = task.getTaskName();
                } else {
                    taskId = -2;
                    JSONArray devArray = JSONArray.parseArray(task.getDevices());
                    for (int i = 0; i < devArray.size(); i++) {
                        JSONObject devObj = devArray.getJSONObject(i);
                        if(deviceCode.equals(devObj.getString("deviceId"))) {
                            taskId = reqTaskId;
                            taskName = task.getTaskName();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                taskId = -1;
            }
        }

        if (taskId > 0) {
            result = getOtaFirmwareFetch(example, deviceCode, type, version, taskId, taskName);
        } else {
            result.put("code", 500);
            result.put("type", type);
        }
        return result;
    }
    private JSONObject getOtaFirmwareFetch(QueryWrapper<Device> example, String deviceCode, String type, String version, int taskId, String taskName) {
        Device device = deviceMapper.selectOne(example);
        String ProductId = device.getProductId();
        Product product = productMapper.selectById(ProductId);
        int firmwareId = product.getFirmwareId();

        QueryWrapper<FirmwareVersion> versionExample = new QueryWrapper<>();
        versionExample.eq("firmware_id", firmwareId);
        versionExample.orderByDesc("release_time");
        FirmwareVersion firmwareVersion = firmwareVersionMapper.selectOne(versionExample);
        String lastVersion = firmwareVersion.getVersion();
        String url = firmwareVersion.getUrl();

        boolean isUpdate = ConvertUtil.versionNotCompare(lastVersion, version);
        JSONObject result = new JSONObject();
        if(isUpdate) {
            long logId = 0L;
            if(taskId > 0) {
                String deviceId = device.getDeviceId();
                String deviceName = device.getDeviceName();

                FirmwareUpdateLog log = new FirmwareUpdateLog();
                log.setFirmwareId(firmwareId);
                log.setTaskId(taskId);
                log.setTaskName(taskName);
                log.setDeviceId(deviceId);
                log.setDeviceName(deviceName);
                log.setVersion(lastVersion);
                log.setStatus(1);
                log.setUpdateTime(new Date());
                firmwareUpdateLogMapper.insert(log);
                logId = log.getLogId();
            }
            try {
                File file = new File(url);
                long fileSize = file.length();

                long cardinality = 256 * 1024; // 256KB转换为字节
                long total;

                if (fileSize <= cardinality) {
                    total = 1; // 如果文件小于等于256KB，则返回1个包
                } else {
                    // 计算需要多少个256KB的包
                    total = fileSize / cardinality;
                    if ((fileSize % cardinality) > 0) {
                        total++;
                    }
                }

                result.put("code", 200);
                result.put("type", type);
                if(logId > 0L) {
                    result.put("taskDataId", logId);
                }
                result.put("chunks", total); // 总包数
                result.put("size", fileSize); // 文件大小byte
                result.put("newVersion", lastVersion);
            } catch (Exception e) {
                logger.error("读取设备{}固件文件失败: {}, 错误: {}", deviceCode, url, e.getMessage());

                result.put("code", 550);
                result.put("type", type);
                if(logId > 0L) {
                    result.put("taskDataId", logId);
                }
            }
        } else {
            result.put("code", 220);
            result.put("type", type);
            result.put("newVersion", lastVersion);
        }
        return result;
    }
    private JSONObject otaSendFetchData(QueryWrapper<Device> example, String type, JSONObject regObj) {
        long taskDataId = 0L;
        int index = 0;
        boolean hasParams = false;
        try {
            if (regObj.containsKey("taskDataId")) {
                taskDataId = regObj.getLongValue("taskDataId");
            }
            if (regObj.containsKey("index")) {
                index = regObj.getIntValue("index");
            }
            if(taskDataId > 0L) {
                QueryWrapper<FirmwareUpdateLog> logExample = new QueryWrapper<>();
                logExample.eq("log_id", taskDataId);
                logExample.lt("status", 3);
                hasParams = firmwareUpdateLogMapper.exists(logExample);
            } else {
                hasParams = true;
            }
        } catch (Exception ignored) {
        }

        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("type", type);
        if(taskDataId > 0L) {
            result.put("taskDataId", taskDataId);
        }

        if(hasParams && index > 0) {
            Device device = deviceMapper.selectOne(example);
            String ProductId = device.getProductId();
            Product product = productMapper.selectById(ProductId);
            int firmwareId = product.getFirmwareId();

            QueryWrapper<FirmwareVersion> versionExample = new QueryWrapper<>();
            versionExample.eq("firmware_id", firmwareId);
            versionExample.orderByDesc("release_time");
            FirmwareVersion firmwareVersion = firmwareVersionMapper.selectOne(versionExample);
            String lastVersion = firmwareVersion.getVersion();
            String url = firmwareVersion.getUrl();

            File file = null;
            long fileSize = 0L;
            try {
                file = new File(url);
                fileSize = file.length();
            } catch (Exception ignored) {
            }
            if (fileSize > 0L) {
                long cardinality = 256 * 1024; // 256KB转换为字节
                long total;

                if (fileSize <= cardinality) {
                    total = 1; // 如果文件小于等于256KB，则返回1个包
                } else {
                    // 计算需要多少个256KB的包
                    total = fileSize / cardinality;
                    if((fileSize % cardinality) > 0) {
                        total++;
                    }
                }

                // 计算当前下载次数占全流程的百分比
                double currentPercentage = (double)index / total * 90.0;
                int progress = (int) Math.round(currentPercentage);


                try (FileInputStream fis = new FileInputStream(file)) {
                    int i = index - 1;
                    long remainingBytes = fileSize - (i * cardinality); // 剩余字节数
                    int readSize = (int) Math.min(cardinality, remainingBytes); // 当前包实际读取大小
                    byte[] buffer = new byte[(int) cardinality];

                    int bytesRead = fis.read(buffer, 0, readSize);
                    if (bytesRead > 0) {
                        // 将字节数组转换为Base64字符串
                        String base64Chunk = Base64.getEncoder().encodeToString(
                                Arrays.copyOf(buffer, bytesRead)
                        );

                        FirmwareUpdateLog log = firmwareUpdateLogMapper.selectById(taskDataId);
                        log.setStatus(2);
                        log.setProgress(progress);
                        log.setUpdateTime(new Date());
                        firmwareUpdateLogMapper.updateById(log);

                        result.put("code", 200);
                        result.put("version", lastVersion);
                        result.put("index", index); // 包索引，从0开始
                        result.put("chunks", total); // 总包数
                        result.put("size", fileSize); // 总包数
                        result.put("data", base64Chunk); // Base64编码的数据
                    } else {
                        result.put("code", 550);
                    }
                } catch (IOException e) {
                    result.put("code", 550);
                }
            }
        }
        return result;
    }

    /**
     * 提交固件升级任务状态
     * @param type 固件升级类型
     * @param deviceCode 设备编号
     * @param regObj 固件升级参数
     * @return 固件升级结果
     */
    private JSONObject otaFirmwareVersionTaskStatus(String type, String deviceCode, JSONObject regObj) {
        JSONObject result = new JSONObject();
        boolean isUpdate = false;
        long taskDataId = 0L;
        int updateStatus = 0;
        if(regObj.containsKey("logId") && regObj.containsKey("taskId") && regObj.containsKey("updateStatus")) {
            try {
                taskDataId = Long.parseLong(regObj.getString("logId"));
                updateStatus = Integer.parseInt(regObj.getString("updateStatus"));
            } catch (Exception ignored) {
            }

            if(taskDataId > 0L) {
                if(updateStatus > 2 && updateStatus < 6) {
                    QueryWrapper<FirmwareUpdateLog> logExample = new QueryWrapper<>();
                    logExample.eq("log_id", taskDataId);
                    logExample.lt("status", 3);
                    boolean hasLog = firmwareUpdateLogMapper.exists(logExample);
                    if (hasLog) {
                        FirmwareUpdateLog log = firmwareUpdateLogMapper.selectOne(logExample);
                        String version = log.getVersion();
                        version = version.replace("v", "");
                        version = version.replace("V", "");
                        log.setStatus(updateStatus);
                        if(updateStatus == 3) {
                            log.setProgress(100);
                        }
                        log.setUpdateTime(new Date());
                        firmwareUpdateLogMapper.updateById(log);

                        if(updateStatus == 3) {
                            Device device = deviceMapper.selectById(deviceCode);
                            device.setFirmwareVersion(version);
                            deviceMapper.updateById(device);
                        }

                        isUpdate = true;
                    }
                }
            }
        }
        if(isUpdate) {
            result.put("code", 200);
            result.put("type", type);
            result.put("taskDataId", taskDataId);
        } else {
            result.put("code", 500);
            result.put("type", type);
            result.put("taskDataId", taskDataId);
        }
        return result;
    }

    /**
     * 处理通用消息
     * @param topic 主题
     * @param payload 消息内容
     */
    public void handleGeneralMessage(String topic, String payload) {
        logger.info("处理通用消息, 主题: {}, 内容: {}", topic, payload);
        // 在这里添加具体的通用消息处理逻辑
    }

    /**
     * 发布消息
     * @param topic 主题
     * @param messageJson 消息内容(JSON)
     * @param qos QoS等级
     */
    public void publishDataMessage(String topic, JSONObject messageJson, int qos) {
        String message = messageJson.toJSONString();
        publishMessage(topic, message, qos);
    }
    public void publishMessage(String topic, String message, int qos) {
        MqttConfig mqttConfig = applicationContext.getBean(MqttConfig.class);
        mqttConfig.publish(topic, message, qos);
    }
}