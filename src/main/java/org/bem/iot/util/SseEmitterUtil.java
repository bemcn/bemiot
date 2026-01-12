package org.bem.iot.util;

import org.bem.iot.entity.SseEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * SSE工具类
 * @author jakybland
 */
public class SseEmitterUtil {
    private static final Map<String, SseEmitter> EMITTER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> USER_MAP = new ConcurrentHashMap<>();
    private static final long DEFAULT_TIMEOUT = 10 * 60 * 1000L; // 10分钟

    /**
     * 创建新的SSE连接
     */
    public static SseEmitter create(String clientId, String param) {
        return create(clientId, param, DEFAULT_TIMEOUT);
    }

    /**
     * 创建新的SSE连接
     */
    public static SseEmitter create(String clientId, String param, long timeout) {
        // 如果已存在则先关闭
        if (EMITTER_MAP.containsKey(clientId)) {
            USER_MAP.remove(clientId);
            EMITTER_MAP.get(clientId).complete();
        }

        SseEmitter emitter = new SseEmitter(timeout);
        EMITTER_MAP.put(clientId, emitter);
        USER_MAP.put(clientId, param);

        // 设置回调
        emitter.onCompletion(() -> {
            USER_MAP.remove(clientId);
            EMITTER_MAP.remove(clientId);
            System.out.println("SSE连接完成: " + clientId);
        });
        emitter.onTimeout(() -> {
            USER_MAP.remove(clientId);
            EMITTER_MAP.remove(clientId);
            System.out.println("SSE连接超时: " + clientId);
        });
        emitter.onError(e -> {
            USER_MAP.remove(clientId);
            EMITTER_MAP.remove(clientId);
            System.out.println("SSE连接错误: " + clientId + ", " + e.getMessage());
        });

        return emitter;
    }

    /**
     * 发送事件给指定客户端
     */
    public static void send(String clientId, SseEvent event) {
        if (EMITTER_MAP.containsKey(clientId)) {
            try {
                EMITTER_MAP.get(clientId).send(
                        SseEmitter.event()
                                .id(event.getEventId())
                                .name(event.getEventName())
                                .data(event.getData())
                );
            } catch (IOException e) {
                USER_MAP.remove(clientId);
                EMITTER_MAP.remove(clientId);
                throw new RuntimeException("发送SSE事件失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 批量发送事件给指定事务的客户端
     */
    public static void sendByTransaction(String param, SseEvent event) {
        for (String clientId : USER_MAP.keySet()) {
            String keyParam = USER_MAP.get(clientId);
            if(keyParam.equals(param) && EMITTER_MAP.containsKey(clientId)) {
                try {
                    EMITTER_MAP.get(clientId).send(
                            SseEmitter.event()
                                    .id(event.getEventId())
                                    .name(event.getEventName())
                                    .data(event.getData())
                    );
                } catch (IOException e) {
                    USER_MAP.remove(clientId);
                    EMITTER_MAP.remove(clientId);
                    throw new RuntimeException("发送SSE事件失败: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 广播事件给所有客户端
     */
    public static void broadcast(SseEvent event) {
        EMITTER_MAP.forEach((clientId, emitter) -> {
            send(clientId, event);
        });
    }

    /**
     * 关闭指定客户端的连接
     */
    public static void complete(String clientId) {
        if (EMITTER_MAP.containsKey(clientId)) {
            EMITTER_MAP.get(clientId).complete();
            USER_MAP.remove(clientId);
            EMITTER_MAP.remove(clientId);
        }
    }

    /**
     * 获取当前连接的客户端数量
     */
    public static int getClientCount() {
        return EMITTER_MAP.size();
    }

    /**
     * 遍历所有客户端执行操作
     */
    public static void forEach(Consumer<Map.Entry<String, SseEmitter>> action) {
        EMITTER_MAP.entrySet().forEach(action);
    }
}
