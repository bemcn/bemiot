package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.SpaceIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间位置
 * @author JiangShiYi
 */
@Data
@TableName("spatial_position")
public class SpacePosition implements Serializable {
    @Serial
    private static final long serialVersionUID = -6464551948692741347L;

    /**
     * 空间位置ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @SpaceIdVerify(groups = Edit.class)
    @TableId(value = "space_id", type = IdType.AUTO)
    private Integer spaceId;

    /**
     * 空间位置
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "空间位置不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "空间位置不能超过50个字符")
    @TableField("space_name")
    private String spaceName;

    /**
     * 上级ID
     */
    @TableField("level_id")
    private Integer levelId;

    /**
     * 级联位置路由(:号前后间隔)
     */
    @TableField("space_route")
    private String spaceRoute;

    /**
     * 级联位置名称
     */
    @TableField("space_route_name")
    private String spaceRouteName;

    /**
     * 排序值
     */
    @TableField("order_num")
    private Integer orderNum;
}
