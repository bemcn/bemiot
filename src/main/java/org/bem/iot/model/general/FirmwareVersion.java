package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品固件版本
 * @author JiangShiYi
 */
@Data
@TableName("firmware_version")
public class FirmwareVersion implements Serializable {
    @Serial
    private static final long serialVersionUID = -9070022713216614334L;

    /**
     * 版本ID
     */
    @TableId(value = "version_id", type = IdType.AUTO)
    private Long versionId;

    /**
     * 固件ID
     */
    @TableField("firmware_id")
    private Integer firmwareId;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 存储地址
     */
    @TableField("url")
    private String url;

    /**
     * 版本发布时间
     */
    @TableField("release_time")
    private Date releaseTime;
}
