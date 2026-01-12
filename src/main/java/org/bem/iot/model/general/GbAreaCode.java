package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * GB28181区域编码
 * @author JiangShiYi
 */
@Data
@TableName("gb_area_code")
public class GbAreaCode implements Serializable {
    @Serial
    private static final long serialVersionUID = -9180565478855741555L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private String id;

    /**
     * 区域名字
     */
    @TableField("position")
    private String position;

    /**
     * 父级ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 区域级别（province-省级 city-市级 area-区级/县级）
     */
    @TableField("level")
    private String level;
}
