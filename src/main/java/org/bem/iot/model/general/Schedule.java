package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.ScheduleIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务
 * @author JiangShiYi
 */
@Data
@TableName("schedule")
public class Schedule implements Serializable {
    @Serial
    private static final long serialVersionUID = -9073982877138257409L;

    /**
     * 定时任务ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @ScheduleIdVerify(groups = Edit.class)
    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Integer scheduleId;

    /**
     * 定时任务名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "定时任务名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "定时任务名称不能超过50个字符")
    @TableField("schedule_name")
    private String scheduleName;

    /**
     * 定时任务组 device：设备物模型  camera：视频监控  database:数据库  monitor：监控中心
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "定时任务组不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 10, message = "定时任务名称不能超过10个字符")
    @TableField("schedule_group")
    private String scheduleGroup;

    /**
     * 定时器任务类型 （字典）
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "定时器任务类型不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "定时器任务类型不能超过20个字符")
    @TableField("schedule_type")
    private String scheduleType;

    /**
     * 目标关联ID
     */
    @NotNull(groups = Edit.class, message = "目标关联ID不能为空")
    @TableField("correlation_id")
    private Long correlationId;

    /**
     * 目标关联键
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "目标关联键不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 100, message = "目标关联键不能超过100个字符")
    @TableField("correlation_key")
    private String correlationKey;

    /**
     * 运行类型 0：间隔轮循 1：整点执行 2：每日 3：每周 4：每月 5：每年
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "运行类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "运行类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 5, message = "运行类型提交错误")
    @TableField("run_type")
    private Integer runType;

    /**
     * 执行时间
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "执行时间不能为空")
    @TableField("run_times")
    private String runTimes;

    /**
     * 执行规则
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "执行规则不能为空")
    @TableField("run_action")
    private String runAction;

    /**
     * 执行错误策略 0：跳过继续执行 1：放弃执行
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "执行错误策略不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "执行错误策略提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "执行错误策略提交错误")
    @TableField("error_policy")
    private Integer errorPolicy;

    /**
     * 任务状态 1：停止 2：正常  3：异常
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "任务状态不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "任务状态提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "任务状态提交错误")
    @TableField("status")
    private Integer status;

    /**
     * 异常原因
     */
    @TableField("abnormal")
    private String abnormal;

    /**
     * 异常消息
     */
    @TableField("abnormal_msg")
    private String abnormalMsg;

    /**
     * 异常时间
     */
    @TableField("abnormal_time")
    private Date abnormalTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
