package org.bem.iot.model.video;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * SIP服务器配置
 * @author JiangShiYi
 */
@Data
public class SipConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -7352614566189125886L;

    /**
     * SIP ID
     */
    private String sipId;

    /**
     * SIP服务器域
     */
    private String sipDomain;

    /**
     * SIP服务器地址
     */
    private String sipAddress;

    /**
     * SIP服务器端口
     */
    private Integer sipPort;

    /**
     * SIP认证密码
     */
    private String sipPass;

    /**
     * 信令协议 UDP / TCP
     */
    private String protocol;

    /**
     * 视频传输协议 UDP / TCP Active / TCP Passive
     */
    private String transmission;

    /**
     * 心跳间隔 单位：秒 (30)
     */
    private Integer heartInterval;

    /**
     * 注册有效期 单位：秒 (3600)
     */
    private Integer regExpiration;
}
