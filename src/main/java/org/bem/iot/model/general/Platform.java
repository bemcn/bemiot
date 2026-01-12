package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.PlatformIdVerify;
import org.bem.iot.validate.PlatformNoIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 第3方平台接入
 * @author JiangShiYi
 */
@Data
@TableName("platform")
public class Platform implements Serializable {
    @Serial
    private static final long serialVersionUID = 1326210572178931629L;

    /**
     * 编号
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "编号不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "编号不能超过50个字符")
    @PlatformNoIdVerify(groups = Add.class)
    @PlatformIdVerify(groups = Edit.class)
    @TableId(value = "platform_id", type = IdType.NONE)
    private String platformId;

    /**
     * 平台名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "平台名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "平台名称不能超过50个字符")
    @TableField("platform_name")
    private String platformName;

    /**
     * 运用Key
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "运用Key不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 255, message = "运用Key不能超过255个字符")
    @TableField("app_key")
    private String appKey;

    /**
     * 安全密钥
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "安全密钥不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 255, message = "安全密钥不能超过255个字符")
    @TableField("secret_key")
    private String secretKey;

    /**
     * 接入类型 1：运用接口 2：第3方登录 3：数据接口
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "接入类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "接入类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "接入类型提交错误")
    @TableField("access_type")
    private Integer accessType;

    /**
     * 认证跳转地址
     */
    @TableField("auth_jump_url")
    private String authJumpUrl;

    /**
     * 绑定注册URL
     */
    @TableField("bind_reg_url")
    private String bindRegUrl;

    /**
     * 跳转登录URL
     */
    @TableField("login_jump_url")
    private String loginJumpUrl;

    /**
     * 错误提示URL
     */
    @TableField("error_url")
    private String errorUrl;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否系统内置 0：是 1：否
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 图标
     */
    @TableField("icon_img")
    private String iconImg;

    /**
     * 状态 0：停用 1：启用
     */
    @TableField("status")
    private Integer status;
}
