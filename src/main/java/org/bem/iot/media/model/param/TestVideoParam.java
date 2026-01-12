package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试流参数
 */
@Data
public class TestVideoParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 6566889283128058937L;

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
     * 视频宽
     */
    private Integer width = 1920;

    /**
     * 视频高
     */
    private Integer height = 1080;

    /**
     * 帧率
     */
    private Integer fps = 25;

    /**
     * 比特率
     */
    private Integer bitRate = 5000000;

    /**
     * 自动关流
     */
    private Integer autoClose = 1;

    /**
     * 开启hls转码
     */
    private Integer enableHls = 1;

    /**
     * 开启rtsp/webrtc转码
     */
    private Integer enableRtsp = 1;

    /**
     * 开启rtmp/flv转码
     */
    private Integer enableRtmp = 1;

    /**
     * 开启ts/ws转码
     */
    private Integer enableTs = 0;

    /**
     * 开启转fmp4
     */
    private Integer enableFmp4 = 0;

    /**
     * 开启mp4录制
     */
    private Integer enableMp4 = 0;

    /**
     * mp4录制切片大小
     */
    private Integer mp4MaxSecond = 3600;

}
