package org.bem.iot.media.model.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关闭流请求参数
 **/
@Data
public class CloseStreamsParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -8759460315385885896L;

    /**
     * app
     */
    private String app;

    /**
     * 流id
     */
    private String stream;

    /**
     * 是否强制关闭
     */
    private Integer force = 1;

    /**
     * 流的协议
     */
    private String schema;


}
