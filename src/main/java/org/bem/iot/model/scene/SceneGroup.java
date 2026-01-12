package org.bem.iot.model.scene;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.SceneGroupIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 联动分组
 * @author JiangShiYi
 */
@Data
@TableName("scene_group")
public class SceneGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = -8677631444481333381L;

    /**
     * 场景分组ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @SceneGroupIdVerify(groups = Edit.class)
    @TableId(value = "scene_group_id", type = IdType.AUTO)
    private Integer sceneGroupId;

    /**
     * 分组名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "分组名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "分组名称不能超过20个字符")
    @TableField("group_name")
    private String groupName;

    /**
     * 排序值
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "排序值不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "排序值提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 2147483600, message = "排序值提交错误")
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
