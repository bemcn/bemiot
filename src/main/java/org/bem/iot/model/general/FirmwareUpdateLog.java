package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品固件升级日志
 * @author JiangShiYi
 */
@Data
@TableName("firmware_update_log")
public class FirmwareUpdateLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 3209235615562080692L;

    /**
     * ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 固件ID
     */
    @TableField("firmware_id")
    private Integer firmwareId;

    /**
     * 任务ID
     */
    @TableField("task_id")
    private Integer taskId;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String taskName;

    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    /**
     * 版本号
     */
    @TableField(exist = false)
    private String version;

    /**
     * 升级状态 1：待推送 2：升级中 3：升级成功 4：升级失败 5：停止
     */
    @TableField("status")
    private Integer status;

    /**
     * 升级进度 0-100
     */
    @TableField("progress")
    private Integer progress;

    /**
     * 状态更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("update_time")
    private Date updateTime;
}
