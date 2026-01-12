package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.ProtocolIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通讯协议
 * @author JiangShiYi
 */
@Data
@TableName("protocols")
public class Protocols implements Serializable {
    @Serial
    private static final long serialVersionUID = 7054044407384189091L;

    /**
     * 协议ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @ProtocolIdVerify(groups = Edit.class)
    @TableId(value = "protocol_id", type = IdType.AUTO)
    private Integer protocolId;

    /**
     * 协议名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "协议名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "协议名称不能超过20个字符")
    @TableField("protocol_name")
    private String protocolName;

    /**
     * 请求格式（上报）
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "请求格式不能为空")
    @TableField("request")
    private String request;

    /**
     * 响应格式（下发）
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "响应格式不能为空")
    @TableField("response")
    private String response;

    /**
     * 注册认证
     */
    @TableField("registration")
    private String registration;

    /**
     * 心跳包
     */
    @TableField("heartbeat")
    private String heartbeat;

    /**
     * 内置协议 0：否 1:是
     */
    @TableField("built")
    private Integer built;
}
