package org.bem.iot.global;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 缓存监控全局变量
 */
@Component
public class CacheMonitorGlobal {
    /**
     * 主进程核心CPU消耗
     */
    public static volatile BigDecimal lastUsedSysCpu = BigDecimal.ZERO;

    /**
     * 主进程用户CPU消耗
     */
    public static volatile BigDecimal lastUsedUserCpu = BigDecimal.ZERO;

    /**
     * 网络入口流量
     */
    public static volatile BigDecimal lastInputBytes = BigDecimal.ZERO;

    /**
     * 网络出口流量
     */
    public static volatile BigDecimal lastOutputBytes = BigDecimal.ZERO;

    /**
     * 命中次数
     */
    public static volatile long lastKeyHits = 0;

    /**
     * 没命中次数
     */
    public static volatile long lastKeyMisses = 0;

    /**
     * 执行的命令数
     */
    public static volatile long lastQpsSec = 0;
}
