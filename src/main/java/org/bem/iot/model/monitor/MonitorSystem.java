package org.bem.iot.model.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统监控
 * @author JiangShiYi
 */
@Data
@TableName("monitor_system")
public class MonitorSystem implements Serializable {
    @Serial
    private static final long serialVersionUID = -4852125372459909591L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private String id;

    /**
     * 标识
     */
    private String identity;

    /**
     * 监控值
     */
    private Long value;

    /**
     * 时间戳
     */
    private Long timestamp;
}
