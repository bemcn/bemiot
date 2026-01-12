package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 推流代理参数
 *
 * @author lidaofu
 * @since 2023/11/29
 **/
@Data
public class StreamPushProxyParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -9023134077647177363L;

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

    /**
     * 推流代理流地址
     */
    @NotEmpty(message = "推流代理流地址不为空")
    private String url;

    /**
     * rtsp推流时，推流方式
     * 0：tcp，1：udp，2：组播
     */
    private Integer rtpType=0;

    /**
     * 推流代理超时时间，单位秒
     */
    private Integer timeoutSec;
}
