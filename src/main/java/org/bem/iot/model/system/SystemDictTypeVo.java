package org.bem.iot.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统字典类型
 * @author JiangShiYi
 */
@Data
@TableName("system_dict_type")
public class SystemDictTypeVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 3378756656082611060L;

    /**
     * 字典类型ID
     */
    @TableId(value = "dict_type_id", type = IdType.AUTO)
    private Integer dictTypeId;

    /**
     * 字典标识
     */
    @TableField("type_key")
    private String typeKey;

    /**
     * 字典类型
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 字典项统计
     */
    @TableField("dict_count")
    private Integer dictCount;
}
