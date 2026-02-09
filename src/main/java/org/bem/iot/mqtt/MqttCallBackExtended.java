package org.bem.iot.mqtt;

import org.bem.iot.mqtt.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 具有扩展功能的MQTT回调类
 */
public class MqttCallBackExtended implements MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(MqttCallBackExtended.class);

    /**
     * MQTT配置
     */
    private final MqttConfig mqttConfig;

    /**
     * 消息处理线程池
     */
    private final ExecutorService messageProcessingExecutor;

    /**
     * MQTT消息处理服务
     */
    private final MqttMessageService mqttMessageService;

    /**
     * 构造函数
     * @param mqttConfig MQTT配置
     * @param messageProcessingExecutor 消息处理线程池
     * @param mqttMessageService MQTT消息处理服务
     */
    public MqttCallBackExtended(MqttConfig mqttConfig, ExecutorService messageProcessingExecutor, MqttMessageService mqttMessageService) {
        this.mqttConfig = mqttConfig;
        this.messageProcessingExecutor = messageProcessingExecutor;
        this.mqttMessageService = mqttMessageService;
    }

    /**
     * 断开连接时的回调
     * @param cause 断开连接的原因
     */
    @Override
    public void connectionLost(Throwable cause) {
        logger.error("断开服务器连接，原因: {}", cause.getMessage());
        attemptReconnect();
    }

    /**
     * 尝试重新连接
     */
    private void attemptReconnect() {
        logger.info("准备5秒后重新连接MQTT服务器......");
        // 在独立线程中执行重新连接，以避免阻塞回调线程
        messageProcessingExecutor.submit(() -> {
            try {
                Thread.sleep(5000);
                // 尝试重新连接
                mqttConfig.reconnect();
            } catch (InterruptedException e) {
                logger.error("重连线程被中断: ", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("重连过程中发生异常: ", e);
            }
        });
    }

    /**
     * 消息到达时回拨
     * @param topic 主题
     * @param message 消息
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // 将消息处理提交到线程池，实现并行处理
        messageProcessingExecutor.submit(() -> {
            try {
                logger.info("【收到】信息主题:【{}】", topic);
                logger.info("【收到】消息Qos:【{}】", message.getQos());
                logger.info("【收到】信息内容:【{}】", new String(message.getPayload()));
                logger.info("【收到】信息保留:【{}】", message.isRetained());
                
                // 进程已接收消息
                processReceivedMessage(topic, message);
            } catch (Exception e) {
                logger.error("主题错误处理消息: " + topic, e);
            }
        });
    }

    /**
     * 进程已接收消息
     * @param topic 主题
     * @param message 消息内容
     */
    private void processReceivedMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            logger.debug("处理来自主题的消息 {}: {}", topic, payload);

            if (topic.startsWith("/device/")) {
                mqttMessageService.handleDeviceMessage(topic, payload);
            } else if (topic.startsWith("/drive/")) {
                mqttMessageService.handleDriveMessage(topic, payload);
            } else {
                // IOT数据消息
                mqttMessageService.handleGeneralMessage(topic, payload);
            }
        } catch (Exception e) {
            logger.error("错误处理接收消息: ", e);
        }
    }

    /**
     * 消息发布成功时回调<br/>
     * 本端（与设备端相反）：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/active/{deviceCode}/get  发送设备激活验证结果
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/func/{deviceCode}/get  发送平台指令控制
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/info/{deviceCode}/get  发送设备信息
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/ntp/{deviceCode}/get  发送时钟同步数据
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/http/{deviceCode}/set  发送HTTP方式回复OTA升级信息
     * &nbsp;&nbsp;&nbsp;&nbsp; /device/fetch/{deviceCode}/set  发送二进制包方式回复OTA升级信息
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/register/{driveCode}/get  发送驱动注册验证结果
     * &nbsp;&nbsp;&nbsp;&nbsp; /drive/status/{driveCode}/get  发送驱动控制指令 (启动、关闭)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            IMqttAsyncClient client = token.getClient();
            logger.info("【发送】【{}】##########成功发布信息！", client.getClientId());
        } catch (Exception e) {
            logger.error("获取客户端连接信息时出现错误: ", e);
        }
    }
}