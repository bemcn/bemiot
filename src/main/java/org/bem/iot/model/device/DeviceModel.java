package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bem.iot.model.product.Product;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备物模型
 * @author JiangShiYi
 */
@Data
@TableName("product_model")
public class DeviceModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -7689950754905308858L;

    /**
     * 物模型ID
     */
    private String modelId;

    /**
     * 物模型标识（进制修改）
     */
    private String modelIdentity;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 模型类型 0：物理模型 1：虚拟模型
     */
    private Integer modelType;

    /**
     * 模型类别 1：属性 2：服务 3：事件 4：标签
     */
    private Integer modelClass;

    /**
     * 数据类型 int、number、text、timestamp、bool、array、enum
     */
    private String dataType;

    /**
     * 数据定义
     */
    private String dataDefinition;

    /**
     * 是否图表展示 0：否 1：是
     */
    private Integer charts;

    /**
     * 是否实时监测 0：否 1：是
     */
    private Integer monitor;

    /**
     * 是否历史存储 0：否 1：是
     */
    private Integer history;

    /**
     * 是否只读数据 0：否 1：是
     */
    private Integer readonly;

    /**
     * 是否分享权限 0：否 1：是
     */
    private Integer share;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 物模型值
     */
    private String value;

    /**
     * 状态 0：正常 1：离线 2：告警
     */
    private Integer status;

    /**
     * 状态 0：正常 1：离线 2：告警
     */
    private Device device;

    /**
     * 状态 0：正常 1：离线 2：告警
     */
    private Product product;
}
