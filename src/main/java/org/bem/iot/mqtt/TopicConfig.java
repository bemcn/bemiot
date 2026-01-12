package org.bem.iot.mqtt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MQTT 主题配置
 */
@Getter
@Component
public class TopicConfig {
    /**
     * 订阅的Topic
     */
    @Value("#{'${spring.mqtt.subscribe.topics:warn_topic,sensor_data,device_status}'.split(',')}")
    private List<String> subscribeTopics;

    /**
     * 设置订阅的Topic
     * @param topics 订阅的Topic
     */
    public void setSubscribeTopics(List<String> topics) {
        this.subscribeTopics = topics;
    }
}