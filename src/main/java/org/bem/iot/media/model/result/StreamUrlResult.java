package org.bem.iot.media.model.result;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.bem.iot.media.config.MediaServerConfig;
import org.bem.iot.media.model.param.StreamProxyParam;
import org.bem.iot.media.model.param.TestVideoParam;

import java.io.Serial;
import java.io.Serializable;

/**
 * 播放地址结果
 */
@Data
public class StreamUrlResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -6837307423205624018L;

    /**
     * 拉流代理key
     */
    private String key;

    /**
     * app
     */
    private String app;

    /**
     * 流id
     */
    private String stream;

    /**
     * hlsUrl播放地址
     */
    private String hlsUrl;

    /**
     * rtspUrl播放地址
     */
    private String rtspUrl;

    /**
     * rtmpUrl播放地址
     */
    private String rtmpUrl;

    /**
     * wsflv播放地址
     */
    private String wsFlvUrl;

    /**
     * httpflv播放地址
     */
    private String httpFlvUrl;

    /**
     * httpFmp4Url播放地址
     */
    private String httpFmp4Url;

    /**
     * wsFmp4Url播放地址
     */
    private String wsFmp4Url;

    /**
     * httpTsUrl播放地址
     */
    private String httpTsUrl;

    /**
     * wsTsUrl播放地址
     */
    private String wsTsUrl;

    public StreamUrlResult(MediaServerConfig config, StreamProxyParam param, String key) {
        this.app = param.getApp();
        this.key = key;
        this.stream = param.getStream();
        if (param.getEnableRtmp() == 1) {
            this.wsFlvUrl = StrUtil.format("ws://{}:{}/{}/{}.live.flv", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpFlvUrl = StrUtil.format("http://{}:{}/{}/{}.live.flv", config.getMedia_ip(), config.getHttp_port(), app, stream);
            if (config.getRtmp_port() == 1935) {
                this.rtmpUrl = StrUtil.format("rtmp://{}/{}/{}", config.getMedia_ip(), app, stream);
            } else {
                this.rtmpUrl = StrUtil.format("rtmp://{}:{}/{}/{}", config.getMedia_ip(), config.getRtmp_port(), app, stream);
            }
        }
        if (param.getEnableHls() == 1) {
            this.hlsUrl = StrUtil.format("http://{}:{}/{}/{}/hls.m3u8", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }
        if (param.getEnableRtsp() == 1) {
            if (config.getRtsp_port() == 554) {
                this.rtspUrl = StrUtil.format("rtsp://{}/{}/{}", config.getMedia_ip(), app, stream);
            } else {
                this.rtspUrl = StrUtil.format("rtsp://{}:{}/{}/{}", config.getMedia_ip(), config.getRtsp_port(), app, stream);
            }
        }
        if (param.getEnableFmp4() == 1) {
            this.wsFmp4Url = StrUtil.format("ws://{}:{}/{}/{}.live.mp4", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpFmp4Url = StrUtil.format("http://{}:{}/{}/{}.live.mp4", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }

        if (param.getEnableTs() == 1) {
            this.wsTsUrl = StrUtil.format("ws://{}:{}/{}/{}.live.ts", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpTsUrl = StrUtil.format("http://{}:{}/{}/{}.live.ts", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }

    }

    public StreamUrlResult(MediaServerConfig config, TestVideoParam param) {
        this.app = param.getApp();
        this.stream = param.getStream();
        if (param.getEnableRtmp() == 1) {
            this.wsFlvUrl = StrUtil.format("ws://{}:{}/{}/{}.live.flv", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpFlvUrl = StrUtil.format("http://{}:{}/{}/{}.live.flv", config.getMedia_ip(), config.getHttp_port(), app, stream);
            if (config.getRtmp_port() == 1935) {
                this.rtmpUrl = StrUtil.format("rtmp://{}/{}/{}", config.getMedia_ip(), app, stream);
            } else {
                this.rtmpUrl = StrUtil.format("rtmp://{}:{}/{}/{}", config.getMedia_ip(), config.getRtmp_port(), app, stream);
            }
        }
        if (param.getEnableHls() == 1) {
            this.hlsUrl = StrUtil.format("http://{}:{}/{}/{}/hls.m3u8", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }
        if (param.getEnableRtsp() == 1) {
            if (config.getRtsp_port() == 554) {
                this.rtspUrl = StrUtil.format("rtsp://{}/{}/{}", config.getMedia_ip(), app, stream);
            } else {
                this.rtspUrl = StrUtil.format("rtsp://{}:{}/{}/{}", config.getMedia_ip(), config.getRtsp_port(), app, stream);
            }
        }
        if (param.getEnableFmp4() == 1) {
            this.wsFmp4Url = StrUtil.format("ws://{}:{}/{}/{}.live.mp4", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpFmp4Url = StrUtil.format("http://{}:{}/{}/{}.live.mp4", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }

        if (param.getEnableTs() == 1) {
            this.wsTsUrl = StrUtil.format("ws://{}:{}/{}/{}.live.ts", config.getMedia_ip(), config.getHttp_port(), app, stream);
            this.httpTsUrl = StrUtil.format("http://{}:{}/{}/{}.live.ts", config.getMedia_ip(), config.getHttp_port(), app, stream);
        }

    }

    public StreamUrlResult() {
    }
}
