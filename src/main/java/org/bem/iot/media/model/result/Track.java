package org.bem.iot.media.model.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 轨道信息
 */
@Data
public class Track implements Serializable {
    @Serial
    private static final long serialVersionUID = -2963627477450609120L;

    /**
     * 是否是视频
     */
    private Integer is_video;

    /**
     * 编码器id
     */
    private Integer codec_id;

    /**
     * 编码器名称
     */
    private String codec_id_name;

    /**
     * 编码器类型
     */
    private Integer codec_type;

    /**
     * 帧率
     */
    private Integer fps;

    /**
     * 帧数
     */
    private Long  frames;

    /**
     * 时长
     */
    private Long duration;

    /**
     * 比特率
     */
    private Integer bit_rate;

    /**
     * gop间隔
     */
    private Integer gop_interval_ms;

    /**
     * gop大小
     */
    private Integer gop_size;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 关键帧
     */
    private Long key_frames;

    /**
     * 丢包率
     */
    private Float  loss;

    /**
     * 是否就绪
     */
    private Boolean ready;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 采样率
     */
    private Integer sample_rate;

    /**
     * 声道数
     */
    private Integer audio_channel;

    /**
     * 采样位数
     */
    private Integer audio_sample_bit;

}