package org.bem.iot.entity.params;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新设备定位
 * @author jakybland
 */
@Data
public class DeviceLocationParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 1175428466243989831L;

    /**
     * 设备编号
     */
    @NotNull(groups = Edit.class, message = "设备ID不能为空")
    private String deviceId;

    /**
     * 设备定位位置
     */
    private String address;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
