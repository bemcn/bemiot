package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 运用授权
 * @author JiangShiYi
 */
@Data
@TableName("app_auth")
public class AppAuth implements Serializable {
    @Serial
    private static final long serialVersionUID = -7876868779823338848L;

    /**
     * 运用ID
     */
    @TableId(value = "app_id", type = IdType.NONE)
    private String appId;

    /**
     * 运用密钥
     */
    @TableField("secret_key")
    private String secretKey;

    /**
     * 运用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 运用来源
     */
    @TableField("app_source")
    private String appSource;

    /**
     * 运用环境
     */
    @TableField("app_environment")
    private String appEnvironment;

    /**
     * 运用授权
     */
    @TableField("app_auth")
    private String appAuth;

    /**
     * 加密方式 0：NONE 1：AES 2：RSA
     */
    @TableField("secure_type")
    private Integer secureType;

    /**
     * AES密钥
     */
    @TableField("aes_key")
    private String aesKey;

    /**
     * AES IV
     */
    @TableField("aes_iv")
    private String aesIv;

    /**
     * RSA公钥
     */
    @TableField("public_key")
    private String publicKey;

    /**
     * RSA私钥
     */
    @TableField("private_key")
    private String privateKey;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否系统应用
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}

