package org.bem.iot.entity.params;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新设备固件版本号
 * @author jakybland
 */
@Data
public class DeviceFirmwareVersionParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 3044590335002427100L;

    /**
     * 设备编号
     */
    @NotNull(groups = Edit.class, message = "设备ID不能为空")
    private String deviceId;

    /**
     * 版本更新任务ID
     */
    @NotEmpty(message = "任务ID不能为空")
    private Integer taskId;

    /**
     * 更新状态 1：待推送 2：升级中 3：升级成功 4：升级失败 5：停止
     */
    @NotNull(groups = Edit.class, message = "更新状态不能为空")
    private Integer status;

    /**
     * 升级进度 0-100
     */
    @NotNull(groups = Edit.class, message = "升级进度不能为空")
    private Integer progress;
}
