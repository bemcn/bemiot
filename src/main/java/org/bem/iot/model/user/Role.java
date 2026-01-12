package org.bem.iot.model.user;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.RoleIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户权限
 * @author JiangShiYi
 *
 */
@Data
@TableName("role")
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 5150426715259849864L;

    /**
     * 角色ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @RoleIdVerify(groups = Edit.class)
    @ExcelProperty("ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "角色名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "角色名称不能超过20个字符")
    @ExcelProperty("角色名称")
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限（JSON）
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "角色权限不能为空")
    @ExcelIgnore
    @TableField("role_auth")
    private String roleAuth;

    /**
     * 排序值
     */
    @ExcelIgnore
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @TableField("remark")
    private String remark;
}
