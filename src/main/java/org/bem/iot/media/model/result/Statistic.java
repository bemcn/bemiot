package org.bem.iot.media.model.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 内存占用信息
 *
 * @author lidaofu
 * @since 2024/5/20
 **/
@Data
public class Statistic implements Serializable {
    @Serial
    private static final long serialVersionUID = -4274311725954434132L;

    /**
     * 媒体源
     */
    private Long mediaSource;

    /**
     * 多路复用器
     */
    private Long multiMediaSourceMuxer;

    /**
     * TCP服务器
     */
    private Long tcpServer;

    /**
     * TCP会话
     */
    private Long tcpSession;

    /**
     * UDP服务器
     */
    private Long udpServer;

    /**
     * UDP会话
     */
    private Long udpSession;

    /**
     * TCP客户端
     */
    private Long tcpClient;

    /**
     * Socket
     */
    private Long socket;

    /**
     * 帧进出
     */
    private Long frameImp;

    /**
     * 帧
     */
    private Long frame;

    /**
     * 缓冲区
     */
    private Long buffer;

    /**
     * 原始缓冲区
     */
    private Long bufferRaw;

    /**
     * 字符缓冲区
     */
    private Long bufferLikeString;

    /**
     * 缓冲区列表
     */
    private Long bufferList;

    /**
     * rtp 数据包
     */
    private Long rtpPacket;

    /**
     * rtmp 数据包
     */
    private Long rtmpPacket;
}
