package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.bem.iot.model.video.VideoServer;

import java.io.Serial;
import java.io.Serializable;

/**
 * 监控设备参数
 * @author JiangShiYi
 */
@Data
@TableName("device_monitoring")
public class DeviceMonitoring implements Serializable {
    @Serial
    private static final long serialVersionUID = -5262794621781376738L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 服务器标识
     */
    @TableField("server_id")
    private String serverId;

    /**
     * 服务器类型 sip / media
     */
    @TableField("server_type")
    private String serverType;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 端口号
     */
    @TableField("port")
    private Integer port;

    /**
     * 账号
     */
    @TableField("account")
    private String account;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 云台类型 0：无 1：ONVIF 2:Pelco-D 3:Pelco-P 4：VISCA 5：SIP 6：其他
     */
    @TableField("ptz_type")
    private Integer ptzType;

    /**
     * 关联服务器
     */
    @TableField(exist = false)
    private VideoServer server;
}
