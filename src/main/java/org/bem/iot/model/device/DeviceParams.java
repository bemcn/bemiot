package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备驱动参数
 * @author JiangShiYi
 */
@Data
@TableName("device_params")
public class DeviceParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -7150986846597705744L;

    /**
     * 驱动参数ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private String id;

    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 物模型标识
     */
    @TableField("model_identity")
    private String modelIdentity;

    /**
     * 驱动参数ID
     */
    @TableField("params_id")
    private Long paramsId;

    /**
     * 驱动编号
     */
    @TableField("drive_code")
    private String driveCode;

    /**
     * 分组标识 1：设备信息 2：物模型
     */
    @TableField("group_type")
    private Integer groupType;

    /**
     * 参数名称
     */
    @TableField("params_name")
    private String paramsName;

    /**
     * 参数标识
     */
    @TableField("params_key")
    private String paramsKey;

    /**
     * 参数值类型 text int number select radio check 等
     */
    @TableField("params_type")
    private String paramsType;

    /**
     * 参数值
     */
    @TableField("params_value")
    private String paramsValue;

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
