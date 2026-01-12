package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 开始录像参数
 *
 * @author lidaofu
 * @since 2023/3/30
 **/
@Data
public class StartRecordParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -7636855252142241413L;

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

    /**
     * 录像保存目录
     */
    private String customizedPath;

    /**
     * mp4录像切片时间大小
     * 单位秒，置0则采用配置项
     */
    private Long maxSecond = 1L;
}
