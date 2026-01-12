package org.bem.iot.entity;

import lombok.Data;

/**
 * 登录成功返回用户信息
 */
@Data
public class LoginUser {
    /**
     * 账号
     */
    private String userName;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headImg;
}
