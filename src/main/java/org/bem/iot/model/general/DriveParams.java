package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.DriveCodeVerify;
import org.bem.iot.validate.DriveParamsIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备驱动参数
 * @author JiangShiYi
 */
@Data
@TableName("drive_params")
public class DriveParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 6139506464503400014L;

    /**
     * 驱动参数ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DriveParamsIdVerify(groups = Edit.class)
    @TableId(value = "params_id", type = IdType.AUTO)
    private Long paramsId;

    /**
     * 参数标识
     */
    @NotNull(groups = Add.class, message = "参数标识不能为空")
    @Size(groups = Add.class, min = 1, max = 20, message = "参数标识不能超过20个字符")
    @TableField("params_key")
    private String paramsKey;

    /**
     * 驱动编号
     */
    @NotNull(groups = Add.class, message = "驱动编号不能为空")
    @DriveCodeVerify(groups = Add.class)
    @TableField("drive_code")
    private String driveCode;

    /**
     * 参数分组 1：设备信息 2：物模型
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "分组标识不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "分组标识提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "分组标识提交错误")
    @TableField("group_type")
    private Integer groupType;

    /**
     * 参数名称
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "参数名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "参数名称不能超过20个字符")
    @TableField("params_name")
    private String paramsName;

    /**
     * 参数值类型 text int number select radio check 等
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "参数类型不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 10, message = "参数类型不能超过10个字符")
    @TableField("params_type")
    private String paramsType;

    /**
     * 默认值
     */
    @TableField("default_value")
    private String defaultValue;

    /**
     * 可选数据（适用select radio check类型）
     */
    @TableField("show_data")
    private String showData;

    /**
     * 排序值
     */
    @TableField("order_num")
    private Integer orderNum;
}
