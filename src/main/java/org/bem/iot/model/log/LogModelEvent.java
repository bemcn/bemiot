package org.bem.iot.model.log;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;

import java.io.Serial;
import java.io.Serializable;

/**
 * 物模型事件日志
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.log_model_event")
public class LogModelEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = -4745379052646567246L;

    /**
     * 时间戳
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * id
     */
    @TableField("log_id")
    private String logId;

    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 物模型标识
     */
    @TableField("model_identity")
    private String modelIdentity;

    /**
     * 类型<br/>
     * register：设备注册<br/>
     * online: 设备上线<br/>
     * offline：设备下线<br/>
     * reported: 事件上报<br/>
     * read：读属性<br/>
     * readReply：读属性反馈<br/>
     * write：写属性<br/>
     * writeReply：写属性反馈
     */
    @TableField("type")
    private String type;

    /**
     * 数据内容（JSON）
     */
    @TableField("data")
    private String data;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Device device;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private ProductModel model;
}
