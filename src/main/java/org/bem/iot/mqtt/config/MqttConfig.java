package org.bem.iot.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务开始时连接MQTT服务器
 */
@Configuration
public class MqttConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.mqtt.connection-timeout:100}")
    private int connectionTimeout;

    @Value("${spring.mqtt.keep-alive-interval:20}")
    private int keepAliveInterval;

    @Value("${spring.mqtt.max-inflight:10}")
    private int maxInflight;

    @Resource
    private TopicConfig topicConfig;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 客户端对象
     */
    private MqttClient client;

    /**
     * 线程池用于处理消息
     */
    @Getter
    @Autowired
    private ExecutorService messageProcessingExecutor;

    /**
     * 重连任务调度器
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 连接状态标志
     */
    private volatile boolean connecting = false;

    /**
     * bean初始化后连接到服务器
     */
    @PostConstruct
    public void init() {
        this.connect();
    }

    /**
     * 断开连接
     */
    @PreDestroy
    public void disConnect() {
        try {
            connecting = false;
            
            if (messageProcessingExecutor != null && !messageProcessingExecutor.isShutdown()) {
                messageProcessingExecutor.shutdown();
                try {
                    if (!messageProcessingExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        messageProcessingExecutor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    messageProcessingExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
            if (client != null) {
                client.close();
            }
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (MqttException e) {
            logger.error("断开连接时发生Mqtt异常: {}", e.getMessage());
        }
    }

    /**
     * 客户端连接到服务器
     */
    public synchronized void connect() {
        if (connecting) {
            logger.info("已经连接到MQTT服务器，跳过了重复连接尝试");
            return;
        }
        
        connecting = true;
        
        try {
            // 创建MQTT客户端对象
            client = new MqttClient(hostUrl, applicationName, new MemoryPersistence());
            // MQTT连接设置
            MqttConnectOptions options = new MqttConnectOptions();
            // 无论是否清除会话，设置 false 意味着服务器会保留客户端连接记录（订阅主题、qos），
            // 客户端可以在重新连接后断开连接期间接收服务器的消息
            // 设为true味着每次连接到服务器的连接都是新的身份
            options.setCleanSession(true);
            // 设置连接用户名
            options.setUserName(username);
            // 设置连接密码
            options.setPassword(password.toCharArray());
            // 设置超时时间（以秒为单位）
            options.setConnectionTimeout(connectionTimeout);
            // 设置心跳时间（秒），表示服务器每1.5*20秒向客户端发送一次心跳，以检查客户端是否在线
            options.setKeepAliveInterval(keepAliveInterval);
            // 设置最大消息数
            options.setMaxInflight(maxInflight);
            // 设置自动重连
            options.setAutomaticReconnect(true);
            // 设置遗嘱消息主题，如果客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱消息
            options.setWill("willTopic", (applicationName + " 与服务器断开连接").getBytes(), 0, false);
            // 从Spring容器获取MqttMessageService实例，使用限定符指定特定的实现
            MqttMessageService mqttMessageService = applicationContext.getBean("mqttMessageService", MqttMessageService.class);
            // 使用线程池设置回调
            client.setCallback(new MqttCallBackExtended(this, messageProcessingExecutor, mqttMessageService));
            // 连接
            client.connect(options);
            // 订阅所有已配置的主题
            subscribeAllTopics();
            connecting = false;
            logger.info("已成功连接到MQTT服务器并订阅主题");
        } catch (MqttException e) {
            connecting = false;
            logger.error("Mqtt连接过程中出现异常: {}", e.getMessage());
        }
    }

    /**
     * 订阅所有已配置的主题
     */
    private void subscribeAllTopics() {
        try {
            if (topicConfig != null && topicConfig.getSubscribeTopics() != null) {
                String[] topics = topicConfig.getSubscribeTopics().toArray(new String[0]);
                int[] qosLevels = new int[topics.length];
                
                // 设置所有主题使用相同的QoS级别
                // 使用 QoS 2
                Arrays.fill(qosLevels, 2);
                
                // 批量订阅，提高效率
                client.subscribe(topics, qosLevels);
                
                for (String topic : topics) {
                    topic = topic.trim(); // 去除空格
                    if (!topic.isEmpty()) {
                        logger.info("订阅主题: {}", topic);
                    }
                }
            }
        } catch (MqttException e) {
            logger.error("订阅主题时的例外: {}", e.getMessage());
        }
    }

    /**
     * 重新连接
     */
    public synchronized void reconnect() {
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException e) {
                logger.error("关闭旧连接时的例外: {}", e.getMessage());
            }
        }
        logger.info("尝试重新连接MQTT服务器......");
        connect();
    }

    /**
     * 查看客户端连接状态
     */
    public boolean isCloseConnected() {
        if (client == null) {
            return true;
        }
        return !client.isConnected();
    }

    /**
     * 发布消息
     *
     * @param topic 主题
     * @param message 消息
     * @param qos qos 0-2 推荐 2
     */
    public boolean publish(String topic, String message, int qos) {
        if (client == null || isCloseConnected()) {
            logger.error("MQTT客户端未连接，无法发布消息");
            return false;
        }
        
        MqttMessage mqttMessage = new MqttMessage();
        // 0: 最多一次送达，消息可能丢失
        // 1: 至少一次传递时，消息可能会被重复
        // 2: 只有一次交付，既不丢失也不重复
        mqttMessage.setQos(qos);
        // 是否保留最后一条信息
        mqttMessage.setRetained(false);
        // 信息内容
        mqttMessage.setPayload(message.getBytes());
        // 主题目的地，用于发布/订阅消息
        MqttTopic mqttTopic = client.getTopic(topic);
        // 提供追踪消息传递进度的机制
        // 用于跟踪非阻塞发布时的消息传递状态（后台运行）
        MqttDeliveryToken token;
        try {
            // 将指定消息发布到主题，但不要等待投递完成，返回令牌可用于跟踪消息投递状态
            // 一旦该方法返回，消息被客户端接受发布，消息传递将在后台完成，连接可用
            token = mqttTopic.publish(mqttMessage);
            // 使用异步等待，避免长时间阻塞
            token.waitForCompletion(5000); // 设置5秒超时
            return true;
        } catch (MqttException e) {
            logger.error("Mqtt异常: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 订阅主题
     * @param topic 主题
     * @param qos qos 0-2 推荐 2
     */
    public void subscribe(String topic, int qos) {
        if (client == null || isCloseConnected()) {
            logger.error("MQTT客户端未连接，无法订阅主题");
            return;
        }
        
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            logger.error("Mqtt异常: {}", e.getMessage());
        }
    }
}