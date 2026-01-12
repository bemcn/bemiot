package org.bem.iot.controller;

import jakarta.annotation.Resource;
import org.bem.iot.mqtt.MqttConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mqtt")
public class MqttSendController {
    @Resource
    private MqttConfig mqttConfig;

    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam("topic") String topic,
                              @RequestParam("message") String message) {
        boolean publish = mqttConfig.publish(topic, message, 2); //发布消息
        mqttConfig.subscribe(topic,2); //订阅信息
        if (publish) {
            return "ok";
        }
        return "no";
    }
}
