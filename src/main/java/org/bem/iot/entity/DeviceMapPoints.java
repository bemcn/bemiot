package org.bem.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备所在的地图坐标点
 * @author JiangShiYi
 */
@Data
public class DeviceMapPoints implements Serializable {
    @Serial
    private static final long serialVersionUID = -2286539360978049906L;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     */
    private Integer types;

    /**
     * 状态 0：离线 1：在线 2:告警
     */
    private Integer status;

    /**
     * 级联位置名称
     */
    private String spaceRouteName;

    /**
     * 定位方式 0：无 1：自动定位 2：设备定位 3：自定义位置
     */
    private Integer locateMethod;

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
    private String installImg;

    /**
     * 固件版本
     */
    private String firmwareVersion;

}
