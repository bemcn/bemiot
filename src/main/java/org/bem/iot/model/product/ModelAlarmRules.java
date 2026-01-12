package org.bem.iot.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.ModelALArmRulesIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 告警规则
 * @author JiangShiYi
 */
@Data
@TableName("model_alarm_rules")
public class ModelAlarmRules implements Serializable {
    @Serial
    private static final long serialVersionUID = -5743982877579363166L;

    /**
     * 规则ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @ModelALArmRulesIdVerify(groups = Edit.class)
    @TableId(value = "rules_id", type = IdType.AUTO)
    private Long rulesId;

    /**
     * 规则名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "规则名称不能为空")
    @TableField("rules_name")
    private String rulesName;

    /**
     * 产品ID
     */
    @NotEmpty(groups = Add.class, message = "产品ID不能为空")
    @TableField("product_id")
    private String productId;

    /**
     * 物模型标识
     */
    @NotEmpty(groups = Add.class, message = "物模型标识不能为空")
    @TableField("model_identity")
    private String modelIdentity;

    /**
     * 告警等级 1：一般告警 2：重要告警 3：紧急告警
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "告警等级不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "告警等级提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "产品类型提交错误")
    @TableField("alarm_level")
    private Integer alarmLevel;

    /**
     * 告警规则<br/>
     *       格式： link: and/or<br/>
     *       operator： == != > >= < <=<br/>
     *       value: 值(遵循字段数据类型)<br/>
     */
    @NotEmpty(groups = Add.class, message = "告警规则不能为空")
    @TableField("alarm_rules")
    private String alarmRules;

    /**
     * 通知方式 0：所有用户 1：设备所有者 2：指定用户
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "通知方式不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "通知方式提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "通知方式提交错误")
    @TableField("alerts_type")
    private Integer alertsType;

    /**
     * 指定用户
     */
    @TableField("specify_users")
    private String specifyUsers;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 关联物模型
     */
    @TableField(exist = false)
    private ProductModel model;
}
