package org.bem.iot.model.monitor;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务执行统计
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.statistics_schedule_task")
public class StatisticsScheduleTask implements Serializable {
    @Serial
    private static final long serialVersionUID = -6905149012980095520L;

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
     * 执行成功次数
     */
    @TableField("success_number")
    private Long successNumber;

    /**
     * 执行失败次数
     */
    @TableField("fail_number")
    private Long failNumber;

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
