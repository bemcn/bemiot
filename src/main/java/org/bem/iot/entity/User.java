package org.bem.iot.entity;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.RoleIdVerify;
import org.bem.iot.validate.UserIdVerify;
import org.bem.iot.validate.UserNameVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息
 * @author JiangShiYi
 *
 */
@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -5791967245134343130L;

    /**
     * 用户ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @UserIdVerify(groups = Edit.class)
    private Integer userId;

    /**
     * 账号
     */
    @NotEmpty(groups = Add.class, message = "用户账号不能为空")
    @UserNameVerify(groups = Add.class)
    @Size(groups = Add.class, min = 4, max = 20, message = "用户账号长度必须是4-20个字符")
    private String userName;

    /**
     * 密码
     */
    @NotEmpty(groups = Add.class, message = "用户密码不能为空")
    @Size(groups = Add.class, min = 4, max = 20, message = "密码长度必须是6-20个字符")
    private String passWord;

    /**
     * 角色ID
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "角色ID不能为空")
    @RoleIdVerify(groups = { Add.class, Edit.class })
    private Integer roleId;

    /**
     * 昵称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "用户昵称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "用户昵称不能超过20个字符")
    private String nickName;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 性别 0：未知 1：男 2：女
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "性别不能为空")
    @Min(value = 0, message = "性别提交错误")
    @Max(value = 2, message = "性别提交错误")
    private Integer sex;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
