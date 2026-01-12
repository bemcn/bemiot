package org.bem.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.ScheduleIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务提交状态
 * @author JiangShiYi
 */
@Data
public class ScheduleStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = -3826972965647247358L;

    /**
     * 定时任务ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @ScheduleIdVerify(groups = Edit.class)
    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Integer scheduleId;

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
}
