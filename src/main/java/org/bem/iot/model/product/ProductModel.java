package org.bem.iot.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品物模型
 * @author JiangShiYi
 */
@Data
@TableName("product_model")
public class ProductModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 5871528944751189L;

    /**
     * 物模型ID
     */
    @TableId(value = "model_id", type = IdType.NONE)
    private String modelId;

    /**
     * 物模型标识（进制修改）
     */
    @TableField("model_identity")
    private String modelIdentity;

    /**
     * 物模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 模型类型 0：物理模型 1：虚拟模型
     */
    @TableField("model_type")
    private Integer modelType;

    /**
     * 模型类别 1：属性 2：服务 3：事件 4：标签
     */
    @TableField("model_class")
    private Integer modelClass;

    /**
     * 数据类型 int、number、text、timestamp、bool、array、enum
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据定义
     */
    @TableField("data_definition")
    private String dataDefinition;

    /**
     * 是否图表展示 0：否 1：是
     */
    @TableField("charts")
    private Integer charts;

    /**
     * 是否实时监测 0：否 1：是
     */
    @TableField("monitor")
    private Integer monitor;

    /**
     * 是否历史存储 0：否 1：是
     */
    @TableField("history")
    private Integer history;

    /**
     * 是否只读数据 0：否 1：是
     */
    @TableField("readonly")
    private Integer readonly;

    /**
     * 是否分享权限 0：否 1：是
     */
    @TableField("share")
    private Integer share;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 物模型值
     */
    @TableField(exist = false)
    private String value;

    /**
     * 状态 0：正常 1：离线 2：告警
     */
    @TableField(exist = false)
    private Integer status;

    /**
     * 告警规则数量
     */
    @TableField(exist = false)
    private Long alarmRileCount;
}
