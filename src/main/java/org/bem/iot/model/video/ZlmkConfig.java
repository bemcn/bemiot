package org.bem.iot.model.video;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ZLMediaKit服务器配置
 * @author JiangShiYi
 */
@Data
public class ZlmkConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 6762633756989778448L;

    /**
     * 服务器标识（APP）
     */
    private String appId;

    /**
     * 媒体地址
     */
    private String mediaIp;

    /**
     * 媒体端口
     */
    private Integer ports;

    /**
     * 是否自动关闭
     */
    private Boolean autoClose;

    /**
     * 播放模式 Ts / HLs
     */
    private String playerType;

    /**
     * 是否开启音频（enable_audio）
     */
    private Boolean enableAudio;

    /**
     * 是否开启录制（MP4）（enable_mp4）
     */
    private Boolean enableRecord;

    /**
     * 录像存储路径
     */
    private String recordUrl;

    /**
     * 日志存储天数
     */
    private Integer logDays;

    /**
     * 日志存储路径
     */
    private String logUrl;
}
