package org.bem.iot.media.model.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 拉流代理参数
 *
 * @author lidaofu
 * @since 2023/11/29
 **/
@Data
public class StreamProxyParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -4750900851532035839L;

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
     * 代理流地址
     */
    @NotEmpty(message = "代理流地址不为空")
    private String url;

    /**
     * rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播
     */
    private Integer rtpType = 1;

    /**
     * 拉流重试次数,不传此参数或传值<=0时，则无限重试
     */
    private Integer retryCount = 3;

    /**
     * 拉流超时时间，单位:秒
     */
    private Integer timeoutSec = 5;

    /**
     * 开启hls转码
     */
    private Integer enableHls = 0;

    /**
     * 开启rtsp/webrtc转码
     */
    private Integer enableRtsp = 0;

    /**
     * 开启rtmp/flv转码
     */
    private Integer enableRtmp = 1;

    /**
     * 开启ts/ws转码
     */
    private Integer enableTs = 0;

    /**
     * 转协议是否开启音频
     */
    private Integer enableAudio = 1;

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

    /**
     * rtsp倍速
     */
    private BigDecimal rtspSpeed = BigDecimal.ZERO;

    /**
     * 自动关流
     */
    private Integer autoClose = 1;
}
