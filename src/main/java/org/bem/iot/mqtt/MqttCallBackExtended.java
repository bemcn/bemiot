package org.bem.iot.mqtt;

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
     * 构造函数
     * @param mqttConfig MQTT配置
     * @param messageProcessingExecutor 消息处理线程池
     */
    public MqttCallBackExtended(MqttConfig mqttConfig, ExecutorService messageProcessingExecutor) {
        this.mqttConfig = mqttConfig;
        this.messageProcessingExecutor = messageProcessingExecutor;
    }

    /**
     * 断开连接时的回调
     * @param cause 断开连接的原因
     */
    @Override
    public void connectionLost(Throwable cause) {
        logger.error("断开服务器连接，原因: {}", cause.getMessage());
        
        // Attempt to reconnect
        attemptReconnect();
    }

    /**
     * 尝试重新连接
     */
    private void attemptReconnect() {
        logger.info("准备5秒后重新连接MQTT服务器......");
        
        // 在独立线程中执行重新连接，以避免阻塞回调线程
        mqttConfig.getMessageProcessingExecutor().submit(() -> {
            try {
                // 请等待一段时间后再尝试重新连接
                Thread.sleep(5000);
                
                // 尝试重新连接
                mqttConfig.reconnect();
            } catch (InterruptedException e) {
                logger.error("重连线程被中断: ", e);
                Thread.currentThread().interrupt();
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
            
            // Process according to different topics
            if (topic.startsWith("warn_topic")) {
                handleWarningMessage(topic, payload);
            } else if (topic.startsWith("sensor_data")) {
                handleSensorDataMessage(topic, payload);
            } else if (topic.startsWith("device_status")) {
                handleDeviceStatusMessage(topic, payload);
            } else {
                // 通用处理
                handleGenericMessage(topic, payload);
            }
        } catch (Exception e) {
            logger.error("错误处理接收消息: ", e);
        }
    }

    /**
     * 处理警告信息
     * @param topic 主题
     * @param payload 消息内容
     */
    private void handleWarningMessage(String topic, String payload) {
        logger.info("正在处理警告信息, 主题: {}, 内容: {}", topic, payload);
        // Add specific warning processing logic here
    }

    /**
     * 处理传感器数据消息
     * @param topic 主题
     * @param payload 消息内容
     */
    private void handleSensorDataMessage(String topic, String payload) {
        logger.info("处理传感器数据消息, 主题: {}, 内容: {}", topic, payload);
        // 在这里添加特定的传感器数据处理逻辑
    }

    /**
     * 处理设备状态消息
     */
    private void handleDeviceStatusMessage(String topic, String payload) {
        logger.info("处理设备状态消息, 主题: {}, 内容: {}", topic, payload);
        // Add specific device status processing logic here
    }

    /**
     * 处理通用消息
     */
    private void handleGenericMessage(String topic, String payload) {
        logger.info("处理通用消息, 主题: {}, 内容: {}", topic, payload);
        // Add generic message processing logic here
    }

    /**
     * 消息发布成功时回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            IMqttAsyncClient client = token.getClient();
            logger.info("【发送】【{}】##########成功发布信息！", client.getClientId());
        } catch (Exception e) {
            logger.error("获取客户信息时出现错误: ", e);
        }
    }
}