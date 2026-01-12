package org.bem.iot.global;

import org.springframework.stereotype.Component;

/**
 * 消息监控全局变量
 */
@Component
public class MessageMonitorGlobal {
    /**
     * 连接数
     */
    public static volatile long connect = 0L;

    /**
     * 上一次连接数
     */
    public static volatile long lastConnect = 0L;
    /**
     * 离线数
     */
    public static volatile long offline = 0L;

    /**
     * 接收数量
     */
    public static volatile long received = 0L;

    /**
     * 下发数量
     */
    public static volatile long send = 0L;

    /**
     * 事件数量
     */
    public static volatile long event = 0L;
}
