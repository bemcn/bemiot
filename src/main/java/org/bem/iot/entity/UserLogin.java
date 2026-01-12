package org.bem.iot.entity;

import lombok.Data;
import org.bem.iot.model.user.UserInfo;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录返回信息
 * @author jakybland
 */
@Data
public class UserLogin implements Serializable {
    @Serial
    private static final long serialVersionUID = 6729772373346766520L;

    private boolean status;

    private UserInfo user;

    private String clientSource;

    private String accessToken;

    private String refreshToken;

    private String message;
}
