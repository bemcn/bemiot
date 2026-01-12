package org.bem.iot.media.model.param;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关闭测试流参数
 */
@Data
public class CloseTestVideoParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -1169302772101052695L;

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

}
