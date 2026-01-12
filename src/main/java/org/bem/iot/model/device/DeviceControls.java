package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.DeviceControlIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备群控
 * @author JiangShiYi
 */
@Data
@TableName("device_controls")
public class DeviceControls implements Serializable {
    @Serial
    private static final long serialVersionUID = 5029886038433372164L;

    /**
     * 群控ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DeviceControlIdVerify(groups = Edit.class)
    @TableId(value = "control_id", type = IdType.AUTO)
    private Long controlId;

    /**
     * 群控名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "群控名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "群控名称不能超过50个字符")
    @TableField("control_name")
    private String controlName;

    /**
     * 执行方式 immediately：立即执行  delay：延迟执行
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "执行方式不能为空")
    @TableField("control_func")
    private String controlFunc;

    /**
     * 延迟秒数
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "延迟秒数不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "延迟秒数提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 99, message = "延迟秒数不能超过99秒")
    @TableField("delay_second")
    private Integer delaySecond;

    /**
     * 群控规则
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "群控规则不能为空")
    @TableField("control_rule")
    private String controlRule;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
