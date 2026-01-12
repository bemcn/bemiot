package org.bem.iot.media.model.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 流信息
 **/
@Data
public class MediaInfoResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -8321009898644075760L;

    /**
     * app
     */
    private String app;

    /**
     * 流id
     */
    private String stream;

    /**
     * 本协议观看人数
     */
    private Integer readerCount;

    /**
     * 产生源类型，包括 unknown = 0,rtmp_push=1,rtsp_push=2,rtp_push=3,pull=4,ffmpeg_pull=5,mp4_vod=6,device_chn=7
     */
    private Integer originType;

    /**
     * 产生源的url
     */
    private String originUrl;

    /**
     * 产生源的url的类型
     */
    private String originTypeStr;

    /**
     * 观看总数 包括hls/rtsp/rtmp/http-flv/ws-flv
     */
    private Integer totalReaderCount;

    /**
     * schema
     */
    private String schema;

    /**
     * 存活时间，单位:秒
     */
    private Long aliveSecond;

    /**
     * 数据产生速度，单位:byte/s
     */
    private Integer  bytesSpeed;

    /**
     * GMT unix系统时间戳，单位:秒
     */
    private Long createStamp;

    /**
     * 是否录制Hls
     */
    private Boolean isRecordingHLS;

    /**
     * 是否录制mp4
     */
    private Boolean isRecordingMP4;

    /**
     * 虚拟地址
     */
    private String vhost;

    private List<Track> tracks;


}
