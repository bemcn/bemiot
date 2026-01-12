package org.bem.iot.model.statistics;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息月统计
 * @author 自动生成
 */
@Data
public class MsgStatisticsMonth implements Serializable {
    @Serial
    private static final long serialVersionUID = 5252842160875969843L;

    /**
     * ID
     */
    private Integer statisticsId;

    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * 发送次数
     */
    private Long sendCount;

    /**
     * 采集次数
     */
    private Long gatherCount;

    /**
     * 告警次数
     */
    private Long alarmCount;

    /**
     * 事件次数
     */
    private Long eventCount;

    /**
     * 认证次数
     */
    private Long authCount;

    /**
     * 连接次数
     */
    private Long connectCount;

    /**
     * 订阅次数
     */
    private Long subscribeCount;

    /**
     * 路由数量
     */
    private Long routingCount;

    /**
     * 保留消息
     */
    private Long retainCount;

    /**
     * 会话数量
     */
    private Long converCount;
}
