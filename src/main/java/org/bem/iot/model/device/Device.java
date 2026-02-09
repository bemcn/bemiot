package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.validate.DeviceIdVerify;
import org.bem.iot.validate.ProductIdVerify;
import org.bem.iot.validate.SpaceIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 设备信息
 * @author JiangShiYi
 */
@Data
@TableName("device")
public class Device implements Serializable {
    @Serial
    private static final long serialVersionUID = -423433327579954641L;

    /**
     * 设备ID
     */
    @NotNull(groups = Edit.class, message = "设备编号不能为空")
    @DeviceIdVerify(groups = Edit.class)
    @TableId(value = "device_id", type = IdType.NONE)
    private String deviceId;

    /**
     * 设备名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "设备名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "设备名称不能超过20个字符")
    @TableField("device_name")
    private String deviceName;

    /**
     * 产品ID
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "产品id不能为空")
    @ProductIdVerify(groups = { Add.class, Edit.class })
    @TableField("product_id")
    private String productId;

    /**
     * 产品类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     */
    @TableField("types")
    private Integer types;

    /**
     * 分类路由
     */
    @TableField("class_route")
    private String classRoute;

    /**
     * 分组ID
     */
    @TableField("group_id")
    private Integer groupId;

    /**
     * 绑定用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 空间位置Id
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "空间位置id不能为空")
    @SpaceIdVerify(groups = { Add.class, Edit.class })
    @TableField("space_id")
    private Integer spaceId;

    /**
     * 级联位置路由
     */
    @TableField("space_route")
    private String spaceRoute;

    /**
     * 级联位置名称
     */
    @TableField("space_route_name")
    private String spaceRouteName;

    /**
     * 父网关id
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "父网关id不能为空")
    @TableField("gateway_id")
    private String gatewayId;

    /**
     * 定位方式 0：无 1：设备定位 2：自定义位置
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "定位方式不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "定位方式提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "定位方式提交错误")
    @TableField("locate_method")
    private Integer locateMethod;

    /**
     * 开启影子设备 0：禁用 1：启用
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "影子设备启用标识能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "影子设备启用标识提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "影子设备启用标识提交错误")
    @TableField("open_shadow")
    private Integer openShadow;

    /**
     * 设备定位位置
     */
    @TableField("address")
    private String address;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 设备安装图片
     */
    @TableField("install_img")
    private String installImg;

    /**
     * 设备摘要，格式[{"name":"device"},{"chip":"esp8266"}]
     */
    @TableField("summary")
    private String summary;

    /**
     * 当前固件版本
     */
    @TableField("firmware_version")
    private String firmwareVersion;

    /**
     * 主通道号取值（4位）
     */
    @TableField("main_channel")
    private Integer mainChannel;

    /**
     * 主通道号
     */
    @TableField("channel_id")
    private String channelId;

    /**
     * 二维码
     */
    @TableField("er_code")
    private String erCode;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 1：未激活 2：禁用 3：启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 激活时间
     */
    @TableField("active_time")
    private Date activeTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 协议参数
     */
    @TableField(exist = false)
    private String paramsData;

    /**
     * 设备驱动参数
     */
    @TableField(exist = false)
    private List<DeviceParams> params;

    /**
     * 视频协议
     */
    @TableField(exist = false)
    private DeviceMonitoring param;

    /**
     * 在线状态 0：离线 2：在线
     */
    @TableField(exist = false)
    private Integer online;

    /**
     * 告警状态 0：无告警 2：告警
     */
    @TableField(exist = false)
    private Integer alarm;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 关联分组
     */
    @TableField(exist = false)
    private DeviceGroup group;

    /**
     * 关联用户
     */
    @TableField(exist = false)
    private UserInfo user;

    /**
     * 关联上级网关
     */
    @TableField(exist = false)
    private Device gateway;
}
