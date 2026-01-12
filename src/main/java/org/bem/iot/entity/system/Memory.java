package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 内存信息
 */
@Data
public class Memory implements Serializable {
    @Serial
    private static final long serialVersionUID = 6495009103744040931L;

    /**
     * 内存大小
     */
    private String total;

    /**
     * 可用内存
     */
    private String available;

    /**
     * 已用内存
     */
    private String use;

    /**
     * 已提交
     */
    private String virtualInUse;

    /**
     * 最大可提交
     */
    private String virtualMax;

    /**
     * 虚拟内存大小
     */
    private String swapTotal;

    /**
     * 内存数量
     */
    private Integer number;

    /**
     * 内存明细
     */
    private List<MemoryItem> memoryItems;
}
