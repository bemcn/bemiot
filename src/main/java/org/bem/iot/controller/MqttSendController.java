package org.bem.iot.controller;

import jakarta.annotation.Resource;
import org.bem.iot.mqtt.MqttMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mqtt")
public class MqttSendController {
    @Resource
    private MqttMessageService mqttMessageService;

    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam("topic") String topic,
                              @RequestParam("message") String message) {
        // 使用MqttMessageService发布消息
        mqttMessageService.publishMessage(topic, message, 2); //发布消息
        
        // 由于不能直接访问MqttConfig，我们通过MqttMessageService访问（如果需要）
        // 但订阅通常在初始化时完成，不是按需操作
        if (mqttMessageService != null) {
            return "ok";
        }
        return "no";
    }
}
