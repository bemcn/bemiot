package org.bem.iot.entity.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 以太网信息
 */
@Data
public class Network implements Serializable {
    @Serial
    private static final long serialVersionUID = 9074432892625653538L;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String displayName;

    /**
     * 别名
     */
    private String ifAlias;

    /**
     * MAC地址
     */
    private String macAddress;

    /**
     * IPv4地址
     */
    private String ipAddress;

    /**
     * IPv4子网掩码
     */
    private String subnetMask;

    /**
     * IPv6地址
     */
    private String ipAddress6;

    /**
     * IPv6前缀长度
     */
    private Integer prefixLenIp6;

    /**
     * 最大传输单元（MTU),单位:Byte
     */
    private String mtu;

    /**
     * 网速
     */
    private String speed;

    /**
     * 已用内存
     */
    private Long bytesRecv;

    /**
     * 发送字节
     */
    private Long bytesSend;

    /**
     * 收到数据包
     */
    private Long packetsRecv;

    /**
     * 发送数据包
     */
    private Long packetsSend;

    /**
     * 虚拟机网络
     */
    private Boolean isKnownVm;
}
