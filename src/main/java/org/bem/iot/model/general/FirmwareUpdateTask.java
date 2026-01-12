package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.FirmwareIdVerify;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品固件升级任务
 * @author JiangShiYi
 */
@Data
@TableName("firmware_update_task")
public class FirmwareUpdateTask implements Serializable {
    @Serial
    private static final long serialVersionUID = 984081956431612418L;

    /**
     * 任务ID
     */
    @TableId(value = "task_id", type = IdType.AUTO)
    private Integer taskId;

    /**
     * 任务名称
     */
    @NotEmpty(message = "任务名称不能为空")
    @Size(min = 1, max = 50, message = "任务名称不能超过50个字符")
    @TableField("task_name")
    private String taskName;

    /**
     * 固件ID
     */
    @NotNull(message = "固件id不能为空")
    @FirmwareIdVerify()
    @TableField(value = "firmware_id")
    private Integer firmwareId;

    /**
     * 升级版本
     */
    @TableField(value = "version")
    private String version;

    /**
     * 升级范围 1：全部设备 2：指定设备
     */
    @NotNull(message = "升级范围不能为空")
    @Min(value = 1, message = "升级范围提交错误")
    @Max(value = 2, message = "升级范围提交错误")
    @TableField("task_type")
    private Integer taskType;

    /**
     * 设备内容（JSON数组）
     */
    @TableField("devices")
    private String devices;

    /**
     * 设备数量
     */
    @TableField("device_count")
    private Integer deviceCount;

    /**
     * 计划时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("plan_time")
    private Date planTime;

    /**
     * 任务描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;
}
