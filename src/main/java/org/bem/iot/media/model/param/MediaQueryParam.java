package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流查询参数
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class MediaQueryParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -2451128285627033861L;

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
     * 流的协议
     */
    @NotEmpty(message = "流的协议不为空")
    private String schema;

}
