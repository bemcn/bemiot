package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 开启rtp服务
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class OpenRtpServerParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1116277449996491407L;

    /**
     * 接收端口
     * 0则为随机端口
     */
    @NotEmpty(message = "接收端口，0则为随机端口")
    private Integer port;

    /**
     * tcp模式
     * 0 udp 模式，1 tcp 被动模式, 2 tcp 主动模式。 (兼容enable_tcp 为0/1)
     */
    @NotEmpty(message = "0 udp 模式，1 tcp 被动模式, 2 tcp 主动模式。 (兼容enable_tcp 为0/1)")
    private Integer tcpMode;

    /**
     * 流id
     */
    @NotEmpty(message = "流id不为空")
    private String stream;


}
