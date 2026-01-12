package org.bem.iot.model.video;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.VideoServerIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 视频服务
 * @author JiangShiYi
 */
@Data
@TableName("video_server")
public class VideoServer implements Serializable {
    @Serial
    private static final long serialVersionUID = -5818707133934751964L;

    /**
     * 服务ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @VideoServerIdVerify(groups = Edit.class)
    @TableId(value = "server_id", type = IdType.NONE)
    private String serverId;

    /**
     * 服务名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "服务名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "服务名称不能超过50个字符")
    @TableField("server_name")
    private String serverName;

    /**
     * 服务器类型 sip / media
     */
    @TableField("server_type")
    private String serverType;

    /**
     * 服务器配置
     */
    @TableField("service_config")
    private String serviceConfig;

    /**
     * 服务器配置
     */
    @TableField(exist = false)
    private JSONObject config;

    /**
     * 状态 0：禁用 1：启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
