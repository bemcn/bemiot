package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 内存条信息
 */
@Data
public class MemoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 204002521766872070L;

    /**
     * 标签
     */
    private String bankLabel;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 类型
     */
    private String memoryType;

    /**
     * 容量
     */
    private String capacity;

    /**
     * 速率
     */
    private String clockSpeed;
}
