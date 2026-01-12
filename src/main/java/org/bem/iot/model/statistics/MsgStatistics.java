package org.bem.iot.model.statistics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息时统计
 * @author 自动生成
 */
@Data
@TableName("msg_statistics")
public class MsgStatistics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "statistics_id", type = IdType.NONE)
    private Integer statisticsId;

    /**
     * 年
     */
    @TableField("year")
    private Integer year;

    /**
     * 月
     */
    @TableField("month")
    private Integer month;

    /**
     * 日
     */
    @TableField("day")
    private Integer day;

    /**
     * 小时
     */
    @TableField("hour")
    private Integer hour ;

    /**
     * 发送次数
     */
    @TableField("send_count")
    private Long sendCount;

    /**
     * 采集次数
     */
    @TableField("gather_count")
    private Long gatherCount;

    /**
     * 告警次数
     */
    @TableField("alarm_count")
    private Long alarmCount;

    /**
     * 事件次数
     */
    @TableField("event_count")
    private Long eventCount;

    /**
     * 认证次数
     */
    @TableField("auth_count")
    private Long authCount;

    /**
     * 连接次数
     */
    @TableField("connect_count")
    private Long connectCount;

    /**
     * 订阅次数
     */
    @TableField("subscribe_count")
    private Long subscribeCount;

    /**
     * 路由数量
     */
    @TableField("routing_count")
    private Long routingCount;

    /**
     * 保留消息
     */
    @TableField("retain_count")
    private Long retainCount;

    /**
     * 会话数量
     */
    @TableField("conver_count")
    private Long converCount;
}
