package org.bem.iot.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SSH 事件
 * @author jakybland
 */
@Data
public class SseEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1168883672095325768L;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 数据
     */
    private JSONObject data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 构造函数
     * @param eventName 事件名称
     * @param data 数据
     */
    public SseEvent(String eventName, JSONObject data) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventName = eventName;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
