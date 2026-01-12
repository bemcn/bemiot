package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * cpu信息
 */
@Data
public class Cpu implements Serializable {
    @Serial
    private static final long serialVersionUID = 2085471632301338221L;

    /**
     * 处理器ID
     */
    private String processorID;

    /**
     * CPU名称
     */
    private String cpuName;

    /**
     * CPU物理核心数
     */
    private Integer cpuPhysicalNumber;

    /**
     * CPU逻辑核心数
     */
    private Integer cpuLogicalNumber;

    /**
     * 处理器供应商
     */
    private String vendor;

    /**
     * 处理器频率
     */
    private String vendorFreq;

    /**
     * 处理器位数
     */
    private Integer cpuBit;

    /**
     * 处理器的微架构
     */
    private String microarchitecture;
}
