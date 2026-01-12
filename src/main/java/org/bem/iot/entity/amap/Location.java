package org.bem.iot.entity.amap;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 高德地图地理编码信息
 */
@Data
public class Location implements Serializable {
    @Serial
    private static final long serialVersionUID = -7783028337819306507L;

    /**
     * 经度（经度，纬度）
     */
    private BigDecimal longitude;

    /**
     * 纬度（经度，纬度）
     */
    private BigDecimal latitude;

    public Location(BigDecimal longitude, BigDecimal latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
