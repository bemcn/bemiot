package org.bem.iot.mqtt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.bem.iot.model.general.Drive;
import org.bem.iot.mqtt.config.MqttConfig;
import org.bem.iot.service.DriveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * MQTT消息处理服务接口
 */
@Service
public class MqttMessageService {
    private static final Logger logger = LoggerFactory.getLogger(MqttMessageService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    DriveService driveService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 处理驱动消息
     * 本端（与设备端相反）：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/register/{deviceCode}/post  接收驱动注册申请
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/heartbeat/{deviceCode}/post  接收驱动心跳包
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/statistics/{deviceCode}/post  接收驱动统计数据
     * @param topic 主题
     * @param payload 消息内容
     */
    public void handleDriveMessage(String topic, String payload) {
        logger.info("处理驱动消息, 主题: {}, 内容: {}", topic, payload);
        String[] topicArr = topic.split("/");
        if (topicArr.length < 3) {
            logger.error("无效的主题格式: {}", topic);
            return;
        }
        String target = topicArr[1];
        String driveCode = topicArr[2];

        // 是否注册指令
        if("register".equals(target)) {
            JSONObject result = new JSONObject();
            if(driveRegister(driveCode, payload)) {
                result.put("status", "success");
                result.put("message", "注册成功");
            } else {
                result.put("status", "fail");
                result.put("message", "注册失败，提交数据错误");
            }
            publishMessage("/drive/register/" + driveCode + "/get", result.toString(), 2);
        } else {
            // 心跳
            if ("heartbeat".equals(target)) {
                driveService.updateStatus(driveCode, 2);
            } else if ("statistics".equals(target)) {
                driveStatistics(driveCode, payload);
            }
        }
    }
    private boolean driveRegister(String driveCode, String payload) {
        if(driveService.existsNotDriveCode(driveCode)) {
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
                driveService.insert(drive);

                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }
    private void driveStatistics(String driveCode, String payload) {
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

    /**
     * 处理设备消息<br/>
     * 本端（与设备端相反）：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/register/{deviceCode}/post  接收设备注册申请
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/heartbeat/{deviceCode}/post  接收设备心跳包
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/property/{deviceCode}/post  接收属性/功能和监测数据
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/event/{deviceCode}/post  接收事件
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/info/{deviceCode}/post  接收设备信息
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/ntp/{deviceCode}/post  接收时钟同步申请
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/http/{deviceCode}/reply  接收HTTP方式回复OTA升级响应
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/fetch/{deviceCode}/reply  接收二进制包方式回复OTA升级响应
     * @param topic 主题
     * @param payload 消息内容
     */
    public void handleDeviceMessage(String topic, String payload) {
        logger.info("处理设备消息, 主题: {}, 内容: {}", topic, payload);
        // 在这里添加具体的设备消息处理逻辑
    }

    /**
     * 处理设备消息 通用消息
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
     * @param message 消息内容
     * @param qos QoS等级
     */
    public void publishMessage(String topic, String message, int qos) {
        MqttConfig mqttConfig = applicationContext.getBean(MqttConfig.class);
        mqttConfig.publish(topic, message, qos);
    }
}