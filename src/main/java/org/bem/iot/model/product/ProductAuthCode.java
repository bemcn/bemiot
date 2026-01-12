package org.bem.iot.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bem.iot.model.device.Device;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 产品授权码
 * @author JiangShiYi
 */
@Data
@TableName("product_auth_code")
public class ProductAuthCode implements Serializable {
    @Serial
    private static final long serialVersionUID = -5310333840716600113L;

    /**
     * 授权码
     */
    @TableId(value = "auth_code", type = IdType.NONE)
    private String authCode;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 授权设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 绑定用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 状态 1：未使用 2：使用中
     */
    @TableField("status")
    private Integer status;

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

    /**
     * 产品
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 授权设备
     */
    @TableField(exist = false)
    private Device device;

    /**
     * 绑定用户ID
     */
    @TableField(exist = false)
    private Map<String, Object> user;
}
