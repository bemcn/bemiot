package org.bem.iot.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 * @author JiangShiYi
 *
 */
@Data
@TableName("user_info")
public class UserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1533119075936544191L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 账号
     */
    @TableField("user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField("pass_word")
    private String passWord;

    /**
     * 密码盐
     */
    @TableField("pass_salt")
    private String passSalt;

    /**
     * 角色
     */
    @TableField("role_id")
    private Integer roleId;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 头像
     */
    @TableField("head_img")
    private String headImg;

    /**
     * 性别 0：未知 1：男 2：女
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 状态 0：停用 1：启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 上次登录IP地址
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 上次登录时间
     */
    @TableField("last_login_time")
    private Date lastLoginTime;

    /**
     * 角色
     */
    @TableField(exist = false)
    private Role role;
}
