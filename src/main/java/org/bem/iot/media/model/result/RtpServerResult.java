package org.bem.iot.media.model.result;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * rtp服务
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class RtpServerResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -2037692236648114006L;

    /**
     * 接收端口，0则为随机端口
     */
    @NotNull(message = "接收端口，0则为随机端口")
    private Integer port;

    /**
     * 流id
     */
    @NotEmpty(message = "流id不为空")
    private String stream;


}
