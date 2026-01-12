package org.bem.iot.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 运用信息
 * @author jakybland
 */
@Data
public class AppAuthParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 5033136104127391896L;

    /**
     * 运用ID
     */
    @TableId(value = "app_id", type = IdType.NONE)
    private String appId;

    /**
     * 运用名称
     */
    @NotEmpty(message = "运用名称不能为空")
    @Size(min = 1, max = 30, message = "运用名称不能超过30个字符")
    @TableField("app_name")
    private String appName;

    /**
     * 运用来源
     */
    @NotEmpty(message = "运用来源不能为空")
    @Size(min = 1, max = 50, message = "运用来源不能超过50个字符")
    @TableField("app_source")
    private String appSource;

    /**
     * 运用环境
     */
    @NotEmpty(message = "运用环境不能为空")
    @Size(min = 1, max = 20, message = "运用环境不能超过20个字符")
    @TableField("app_environment")
    private String appEnvironment;

    /**
     * 运用授权
     */
    @NotEmpty(message = "运用授权不能为空")
    @Size(min = 1, max = 10, message = "运用环境不能超过10个字符")
    @TableField("app_auth")
    private String appAuth;

    /**
     * 加密方式 0：NONE 1：AES 2：RSA
     */
    @NotNull(message = "加密方式不能为空")
    @Min(value = 0, message = "加密方式提交错误")
    @Max(value = 2, message = "加密方式提交错误")
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
     * 验证AES密钥 - 当secureType为1时不能为空
     */
    @AssertTrue(message = "AES密钥不能为空")
    public boolean isValidAesKey() {
        if (secureType == 1) {
            return StrUtil.isNotEmpty(aesKey);
        }
        return true;
    }

    /**
     * 验证AES IV - 当secureType为1时不能为空
     */
    @AssertTrue(message = "AES IV不能为空")
    public boolean isValidAesIv() {
        if (secureType == 1) {
            return StrUtil.isNotEmpty(aesIv);
        }
        return true;
    }

    /**
     * 验证RSA公钥 - 当secureType为2时不能为空
     */
    @AssertTrue(message = "RSA公钥不能为空")
    public boolean isValidPublicKey() {
        if (secureType == 2) {
            return StrUtil.isNotEmpty(publicKey);
        }
        return true;
    }

    /**
     * 验证RSA私钥 - 当secureType为2时不能为空
     */
    @AssertTrue(message = "RSA私钥不能为空")
    public boolean isValidPrivateKey() {
        if (secureType == 2) {
            return StrUtil.isNotEmpty(privateKey);
        }
        return true;
    }
}
