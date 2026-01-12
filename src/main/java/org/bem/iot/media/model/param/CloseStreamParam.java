package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关闭流请求参数
 **/
@Data
public class CloseStreamParam  implements Serializable {
    @Serial
    private static final long serialVersionUID = -6066915466908334126L;

    /**
     * app
     */
    @NotEmpty(message = "app不为空")
    private String app;

    /**
     * 流id
     */
    @NotEmpty(message = "流id不为空")
    private String stream;

    /**
     * 是否强制关闭
     */
    @NotNull(message = "是否强制关闭不为空")
    private Integer force;

    /**
     * 流的协议
     */
    @NotEmpty(message = "流的协议不为空")
    private String schema;


}
