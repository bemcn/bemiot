package org.bem.iot.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录提交参数
 * @author jakybland
 */
@Data
public class Login implements Serializable {
    @Serial
    private static final long serialVersionUID = -5420436889553911706L;

    /**
     * 用户名
     */
    @NotEmpty(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度必须是4-20个字符")
    private String userName;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    @Size(min = 4, max = 20, message = "密码长度必须是6-20个字符")
    private String userPwd;

    /**
     * 登录code
     */
    @NotEmpty(message = "code不能为空")
    private String code;
}
