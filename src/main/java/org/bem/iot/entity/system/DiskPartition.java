package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 硬盘明细
 */
@Data
public class DiskPartition implements Serializable {
    @Serial
    private static final long serialVersionUID = -6695968459565816648L;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 主ID
     */
    private Integer major;

    /**
     * 次ID
     */
    private Integer minor;

    /**
     * 名称
     */
    private String itemName;

    /**
     * 类型
     */
    private String itemType;

    /**
     * 容量
     */
    private String itemSize;

    /**
     * 挂载点
     */
    private String mountPoint;

    /**
     * 现场识别
     */
    private String identification;
}
