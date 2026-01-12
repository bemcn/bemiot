package org.bem.iot.media.model.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取流列表
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class GetMediaListParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 3493476311076162894L;

    /**
     * app
     */
    private String app;

    /**
     * 流id
     */
    private String stream;

    /**
     * 流的协议
     */
    private String schema;

}
