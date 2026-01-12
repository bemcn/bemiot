package org.bem.iot.global;

import org.springframework.stereotype.Component;

/**
 * 平台监控全局变量
 */
@Component
public class ServerMonitorGlobal {
    /**
     * CPU利用率/10秒
     */
    public static volatile long cpuRate = 0;

    /**
     * 内存使用量/10秒
     */
    public static volatile long memoryUse = 0;

    /**
     * 硬盘读取量/10秒
     */
    public static volatile long distRead = 0;

    /**
     * 硬盘写入量/10秒
     */
    public static volatile long distWrite = 0;

    /**
     * 硬盘上次读取累计
     */
    public static volatile long lastRead = 0;

    /**
     * 硬盘上次写入累计
     */
    public static volatile long lastWrite = 0;

    /**
     * 网络接收量/10秒
     */
    public static volatile long netRecv = 0L;

    /**
     * 网络发送量/10秒
     */
    public static volatile long netSend = 0L;

    /**
     * 网络上次接收累计
     */
    public static volatile long lastRecvBytes = 0L;

    /**
     * 网络上次发送累计
     */
    public static volatile long lastSendBytes = 0L;

    /**
     * 是否首次运行
     */
    public static volatile boolean isFirstRun = true;
}
