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
 * 物模型告警日志
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.log_model_alarm")
public class LogModelAlarm implements Serializable {
    @Serial
    private static final long serialVersionUID = -3331932635775577612L;

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
     * 告警等级 1：一般告警 2：重要告警 3：紧急告警
     */
    @TableField("level")
    private Integer level;

    /**
     * 物模型采集值
     */
    @TableField("alarm_value")
    private String alarmValue;

    /**
     * 判断符号<br/>
     * ==：等于<br/>
     * !=：不等于<br/>
     * >：大于<br/>
     * <：小于<br/>
     * >=：大于等于<br/>
     * <=：小于等于
     */
    @TableField("judgement")
    private String judgement;

    /**
     * 告警阈值
     */
    @TableField("threshold")
    private String threshold;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 告警规则ID
     */
    @TableField("rules_id")
    private Long rulesId;

    /**
     * 告警状态 1：告警中 2：已解除
     */
    @TableField("alarm_status")
    private Long alarmStatus;

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
