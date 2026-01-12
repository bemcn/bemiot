package org.bem.iot.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQTT 回调
 */
public class MqttCallBack implements MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(MqttCallBack.class);

    /**
     * 断开连接时的回调
     * @param cause 断开原因
     */
    @Override
    public void connectionLost(Throwable cause) {
        logger.error("断开服务器连接，原因: {}", cause.getMessage());
    }

    /**
     * 消息到达时回拨
     * @param topic 主题
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        logger.info("【收到】信息主题:【{}】", topic);
        logger.info("【收到】消息Qos:【{}】", message.getQos());
        logger.info("【收到】信息内容:【{}】", new String(message.getPayload()));
        logger.info("【收到】信息保留:【{}】", message.isRetained());
    }

    /**
     * 消息发布成功时回调
     * @param token token
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