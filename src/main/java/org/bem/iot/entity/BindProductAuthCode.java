package org.bem.iot.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.bem.iot.validate.DeviceIdVerify;
import org.bem.iot.validate.ProductAuthCodeDevVerify;
import org.bem.iot.validate.ProductAuthCodeVerify;
import org.bem.iot.validate.UserIdVerify;

import java.io.Serial;
import java.io.Serializable;

/**
 * 绑定产品授权码
 * @author jakybland
 */
@Data
public class BindProductAuthCode implements Serializable {
    @Serial
    private static final long serialVersionUID = -314076074561252925L;

    @NotEmpty(message = "产品ID不能为空")
    @ProductAuthCodeVerify()
    private String authCode;

    @NotEmpty(message = "设备ID不能为空")
    @DeviceIdVerify()
    @ProductAuthCodeDevVerify()
    private String deviceId;

    @NotEmpty(message = "用户ID不能为空")
    @UserIdVerify()
    private Integer userId;

    private String remark;
}
