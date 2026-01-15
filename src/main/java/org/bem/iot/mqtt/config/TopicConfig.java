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
     * -- SETTER --
     *  设置订阅的Topic
     */
    @Value("#{'${spring.mqtt.subscribe.topics:/drive/+/+/post,/device/+/+/post}'.split(',')}")
    private List<String> subscribeTopics;

}