package org.bem.iot.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 设备地图
 * @author JiangShiYi
 */
@Data
public class DeviceMap implements Serializable {
    @Serial
    private static final long serialVersionUID = 2824771002224431780L;

    /**
     * 地图授权密钥（AES加密）
     */
    private String keys;

    /**
     * 地图中心点经度
     */
    private BigDecimal centerLon;

    /**
     * 地图中心点纬度
     */
    private BigDecimal centerLat;

    /**
     * 地图样式
     * <br>normal:标准
     * <br>dark:幻影黑
     * <br>light:月光银
     * <br>whitesmoke:远山黛
     * <br>fresh:草色青
     * <br>grey:雅士灰
     * <br>graffiti:涂鸦
     * <br>macaron:马卡龙
     * <br>blue:靛青蓝
     * <br>darkblue:极夜蓝
     * <br>wine:酱籽
     */
    private String mapStyle;

    /**
     * 地图缩放级别
     * <br>country：国家-6
     * <br>province：省-8
     * <br>city：市-12
     * <br>area：区域-16
     * <br>range：局部-18
     */
    private Integer zoom;

    /**
     * 设备分布
     */
    private List<DeviceMapPoints> devices;
}
