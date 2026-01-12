package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 硬盘信息
 */
@Data
public class DiskStore implements Serializable {
    @Serial
    private static final long serialVersionUID = 3201918902856730588L;

    /**
     * 磁盘序列号
     */
    private String serial;

    /**
     * 磁盘名称
     */
    private String diskName;

    /**
     * 磁盘模型
     */
    private String diskModel;

    /**
     * 磁盘大小 bit
     */
    private Long diskSize;

    /**
     * I/O队列长度
     */
    private Long currentQueueLength;

    /**
     * 读取字节
     */
    private Long readBytes;

    /**
     * 读取数量
     */
    private Long reads;

    /**
     * 写入字节
     */
    private Long writeBytes;

    /**
     * 写入数量
     */
    private Long writes;

    /**
     * 统计更新时间
     */
    private String updateTime;

    /**
     * 读写操作时长（毫秒）
     */
    private Long transferTime;

    /**
     * 磁盘数量
     */
    private Integer number;

    /**
     * 磁盘明细
     */
    private List<DiskPartition> diskPartitions;
}
