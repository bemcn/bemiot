package org.bem.iot.entity.params;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.DeviceIdVerify;
import org.bem.iot.validate.SpaceIdVerify;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 编辑设备信息
 * @author jakybland
 */
@Data
public class DeviceEditParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 6054172269676674996L;

    /**
     * 设备ID
     */
    @NotNull(message = "id不能为空")
    @DeviceIdVerify()
    private String deviceId;

    /**
     * 设备名称
     */
    @NotEmpty(message = "设备名称不能为空")
    @Size(min = 1, max = 20, message = "设备名称不能超过20个字符")
    private String deviceName;

    /**
     * 分组ID
     */
    private Integer groupId;

    /**
     * 空间位置Id
     */
    @NotNull(message = "空间位置id不能为空")
    @SpaceIdVerify()
    private Integer spaceId;

    /**
     * 父网关id
     */
    @NotEmpty(message = "父网关id不能为空")
    private String gatewayId;

    /**
     * 定位方式 0：无 1：自动定位 2：设备定位 3：自定义位置
     */
    @NotNull(message = "定位方式不能为空")
    @Min(value = 0, message = "定位方式提交错误")
    @Max(value = 3, message = "定位方式提交错误")
    private Integer locateMethod;

    /**
     * 开启影子设备 0：禁用 1：启用
     */
    @NotNull(message = "影子设备启用标识能为空")
    @Min(value = 0, message = "影子设备启用标识提交错误")
    @Max(value = 1, message = "影子设备启用标识提交错误")
    private Integer openShadow;

    /**
     * 设备定位位置
     */
    private String address;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 设备安装图片
     */
    private String installImg;

    /**
     * 当前固件版本
     */
    private String firmwareVersion;

    /**
     * 备注
     */
    private String remark;

    /**
     * 协议参数
     */
    private String paramsData;
}
