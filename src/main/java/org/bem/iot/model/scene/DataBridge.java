package org.bem.iot.model.scene;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.DataBridgeIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据桥接
 * @author your-name
 */
@Data
@TableName("data_bridge")
public class DataBridge implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标识ID
     */
    @NotBlank(groups = {Edit.class}, message = "标识ID不能为空")
    @DataBridgeIdVerify(groups = {Edit.class})
    @TableId(value = "bridge_id", type = IdType.NONE)
    private String bridgeId;

    /**
     * 桥接名称
     */
    @NotBlank(groups = {Add.class, Edit.class}, message = "桥接名称不能为空")
    @Size(groups = {Add.class, Edit.class}, min = 1, max = 50, message = "桥接名称不能超过50个字符")
    @TableField("bridge_name")
    private String bridgeName;

    /**
     * 桥接方向  input：输入  output：输出
     */
    @NotBlank(groups = {Add.class, Edit.class}, message = "桥接方向不能为空")
    @Pattern(groups = {Add.class, Edit.class}, regexp = "input|output", message = "桥接方向只能为input或output")
    @TableField("bridge_direction")
    private String bridgeDirection;

    /**
     * 桥接类型  http：Http推送  mqtt：Mqtt桥接   database：数据库存储
     */
    @NotBlank(groups = {Add.class, Edit.class}, message = "桥接类型不能为空")
    @Pattern(groups = {Add.class, Edit.class}, regexp = "http|mqtt|database", message = "桥接类型只能为http、mqtt或database")
    @TableField("bridge_type")
    private String bridgeType;

    /**
     * http请求地址类型
     */
    @TableField("http_url_type")
    private String httpUrlType;

    /**
     * http请求地址
     */
    @TableField("http_url")
    private String httpUrl;

    /**
     * http请求方法 GET  POST
     */
    @TableField("http_method")
    private String httpMethod;

    /**
     * http请求头
     */
    @TableField("http_header")
    private String httpHeader;

    /**
     * http请求参数
     */
    @TableField("http_params")
    private String httpParams;

    /**
     * http请求体
     */
    @TableField("http_body")
    private String httpBody;

    /**
     * mqtt服务地址
     */
    @TableField("mqtt_url")
    private String mqttUrl;

    /**
     * mqtt客户端ID
     */
    @TableField("mqtt_client_id")
    private String mqttClientId;

    /**
     * mqtt用户名
     */
    @TableField("mqtt_username")
    private String mqttUsername;

    /**
     * mqtt密码
     */
    @TableField("mqtt_password")
    private String mqttPassword;

    /**
     * mqtt数据输入/输出主题
     */
    @TableField("mqtt_topic")
    private String mqttTopic;

    /**
     * mqtt版本
     */
    @TableField("mqtt_version")
    private String mqttVersion;

    /**
     * mqtt连接超时时长
     */
    @TableField("mqtt_timeout")
    private Long mqttTimeout;

    /**
     * mqtt Keep Alive
     */
    @TableField("mqtt_keep_alive")
    private Long mqttKeepAlive;

    /**
     * mqtt开启自动连接 0：关闭 1：开启
     */
    @TableField("mqtt_auto_link")
    private Short mqttAutoLink;

    /**
     * mqtt Clean Session 0：关闭 1：开启
     */
    @TableField("mqtt_clean_session")
    private Short mqttCleanSession;

    /**
     * 数据库源 MySQL  SQLServer  PostgreSQL  Oracle
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 数据库连接地址
     */
    @TableField("data_url")
    private String dataUrl;

    /**
     * 数据库用户名
     */
    @TableField("data_username")
    private String dataUsername;

    /**
     * 数据库密码
     */
    @TableField("data_password")
    private String dataPassword;

    /**
     * 数据库名称
     */
    @TableField("data_name")
    private String dataName;

    /**
     * 执行SQL语句
     */
    @TableField("data_sql")
    private String dataSql;
    
    /**
     * 状态
     */
    @TableField("status")
    private Integer status;
}