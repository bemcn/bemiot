package org.bem.iot.mqtt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MQTT相关线程池配置
 */
@Configuration
public class MqttExecutorConfig {

    /**
     * MQTT消息处理线程池
     *
     * @return ExecutorService
     */
    @Bean(name = "messageProcessingExecutor")
    public ExecutorService messageProcessingExecutor() {
        return new ThreadPoolExecutor(
                5, // 核心线程数
                10, // 最大线程数
                60L, // 存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), // 队列大小
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("mqtt-message-thread-" + System.currentTimeMillis());
                    return thread;
                }
        );
    }
}