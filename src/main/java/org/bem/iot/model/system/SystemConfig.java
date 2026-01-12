package org.bem.iot.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统参数
 * @author JiangShiYi
 */
@Data
@TableName("system_config")
public class SystemConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -819122682616955498L;

    /**
     * 参数主键
     */
    @TableId(value = "config_key", type = IdType.NONE)
    private String configKey;

    /**
     * 参数名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 参数键值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 参数分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
