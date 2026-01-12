package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.DeviceGroupIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备分组
 * @author JiangShiYi
 */
@Data
@TableName("device_group")
public class DeviceGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = -7451306860174986988L;

    /**
     * 分组ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DeviceGroupIdVerify(groups = Edit.class)
    @TableId(value = "group_id", type = IdType.AUTO)
    private Integer groupId;

    /**
     * 分组名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "分组名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "分组名称不能超过20个字符")
    @TableField("group_name")
    private String groupName;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 排序值
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "排序值不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "排序值提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 2147483600, message = "排序值提交错误")
    @TableField("order_num")
    private Integer orderNum;
}
