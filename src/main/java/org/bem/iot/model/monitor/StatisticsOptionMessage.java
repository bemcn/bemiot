package org.bem.iot.model.monitor;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息操作统计
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.statistics_option_message")
public class StatisticsOptionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -2044285515625434487L;

    /**
     * 时间戳
     */
    @ExcelProperty(value="创建时间", index=0)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * ID
     */
    @TableField("log_id")
    private String logId;

    /**
     * 接收数量
     */
    @TableField("received_number")
    private Long receivedNumber;

    /**
     * 下发数量
     */
    @TableField("send_number")
    private Long sendNumber;

    /**
     * 事件数量
     */
    @TableField("event_number")
    private Long eventNumber;

    /**
     * 告警数量
     */
    @TableField("alarm_number")
    private Long alarmNumber;

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
     * 时
     */
    @TableField("hour")
    private Integer hour;
}
