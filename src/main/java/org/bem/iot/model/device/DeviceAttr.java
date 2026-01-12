package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 档案属性
 * @author JiangShiYi
 */
@Data
@TableName("device_attr")
public class DeviceAttr implements Serializable {
    @Serial
    private static final long serialVersionUID = 6336336846387029362L;

    /**
     * 属性ID
     */
    @TableId(value = "dev_attr_id", type = IdType.NONE)
    private String devAttrId;

    @TableField("attr_id")
    private Long attrId;

    /**
     * 产品ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 分类路由
     */
    @TableField("class_route")
    private String classRoute;

    /**
     * 字段名称
     */
    @TableField("field_key")
    private String fieldKey;

    /**
     * 字段标题
     */
    @TableField("field_label")
    private String fieldLabel;

    /**
     * 字段类型 input：输入框 text：文本框 date：日期 img：上传图片
     */
    @TableField("field_type")
    private String fieldType;

    /**
     * 字段值
     */
    @TableField("field_value")
    private String fieldValue;

    /**
     * 关联设备
     */
    @TableField(exist = false)
    private Device device;
}
