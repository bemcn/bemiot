package org.bem.iot.model.log;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务执行日志
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.log_schedule_task")
public class LogScheduleTask  implements Serializable {
    @Serial
    private static final long serialVersionUID = 7860558277770795814L;

    /**
     * 时间戳
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * id
     */
    @TableField("log_id")
    private String logId;

    /**
     * 任务名称
     */
    @TableField("schedule_name")
    private String scheduleName;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 执行方式
     */
    @TableField("run_type")
    private String runType;

    /**
     * 状态 1：成功 2：失败
     */
    @TableField("status")
    private Integer status;
}
