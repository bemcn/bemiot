package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 转码参数
 */
@Data
public class TranscodeParam  implements Serializable {
    @Serial
    private static final long serialVersionUID = -9028409656160718888L;

    /**
     * url(rtmp协议只支持H264)
     */
    @NotEmpty(message = "url不为空")
    private String url;

    /**
     * 转码后推的app
     */
    @NotEmpty(message = "转码后推的app不为空")
    private String app;

    /**
     * 是否开启音频
     */
    private Boolean enableAudio=true;

    /**
     * 转码后推的stream
     */
    @NotEmpty(message = "转码后推的stream不为空")
    private String stream;

    /**
     * 修改分辨率宽(不需要则置为空)
     */
    private Integer scaleWidth = 0;

    /**
     * 修改分辨率高(不需要则置为空)
     */
    private Integer scaleHeight=0;
}
