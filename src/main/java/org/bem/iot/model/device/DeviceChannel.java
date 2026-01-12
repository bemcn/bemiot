package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.video.VideoServer;
import org.bem.iot.validate.DeviceChannelIdVerify;
import org.bem.iot.validate.DeviceIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备通道
 * @author JiangShiYi
 */
@Data
@TableName("device_channel")
public class DeviceChannel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1243467093228255824L;

    /**
     * 通道ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DeviceChannelIdVerify(groups = Edit.class)
    @TableId(value = "channel_id", type = IdType.NONE)
    private String channelId;

    /**
     * 设备ID
     */
    @NotNull(groups = Add.class, message = "设备id不能为空")
    @DeviceIdVerify(groups = Add.class)
    @TableField("device_id")
    private String deviceId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 通道名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "通道名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "通道名称不能超过50个字符")
    @TableField("channel_name")
    private String channelName;

    /**
     * 通道类型（3位）
     */
    @TableField("channel_type")
    private Integer channelType;

    /**
     * 通道序号(SIP)
     */
    @TableField("channel_number")
    private Integer channelNumber;

    /**
     * 通道号
     */
    @TableField("channel")
    private Integer channel;

    /**
     * SMTP地址
     */
    @TableField("smtp_url")
    private String smtpUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 推流状态
     */
    @TableField(exist = false)
    private Boolean pushStatus;

    /**
     * 录像状态
     */
    @TableField(exist = false)
    private Boolean recordStatus;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 关联设备
     */
    @TableField(exist = false)
    private Device device;

    /**
     * 关联设备参数
     */
    @TableField(exist = false)
    private DeviceMonitoring monitoring;

    /**
     * 关联服务器
     */
    @TableField(exist = false)
    private VideoServer server;
}
