package org.bem.iot.model.scene;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.SceneIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 场景联动
 * @author JiangShiYi
 */
@Data
@TableName("scene_linkage")
public class SceneLinkage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1347927782519201679L;

    /**
     * 场景联动ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @SceneIdVerify(groups = Edit.class)
    @TableId(value = "scene_id", type = IdType.AUTO)
    private Long sceneId;

    /**
     * 分组ID
     */
    @TableField("scene_group_id")
    private Integer sceneGroupId;

    /**
     * 场景名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "场景名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "场景名称不能超过50个字符")
    @TableField("scene_name")
    private String sceneName;

    /**
     * 执行方式 1：触发响应 2：循环执行 3：定时执行
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "执行条件不能为空")
    @TableField("execution_method")
    private Integer executionMethod;

    /**
     * 循环间隔（秒）
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "静默周期不能为空")
    @TableField("silent_period")
    private Integer silentPeriod;

    /**
     * 定时设置
     */
    @TableField("time_conditions")
    private String timeConditions;

    /**
     * 规则引擎
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "规则引擎不能为空")
    @TableField("el_data")
    private String elData;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 0：停用 1：启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 关联分组
     */
    @TableField(exist = false)
    private SceneGroup group;
}
