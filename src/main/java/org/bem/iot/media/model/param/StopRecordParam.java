package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 停止录像
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class StopRecordParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -2397015731792669590L;

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
     * 录像类型
     * 0为hls，1为mp4,2:hls-fmp4,3:http-fmp4,4:http-ts 当0时需要开启配置分片持久化
     */
    @NotNull(message = "录像类型不为空")
    private Integer type;

}
