package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.CertificateIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 证书
 * @author JiangShiYi
 */
@Data
@TableName("certificate")
public class Certificate implements Serializable {
    @Serial
    private static final long serialVersionUID = 6589813320716316477L;

    /**
     * 证书ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @CertificateIdVerify(groups = Edit.class)
    @TableId(value = "certificate_id", type = IdType.AUTO)
    private Integer certificateId;

    /**
     * 证书名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "证书名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "证书名称不能超过20个字符")
    @TableField("certificate_name")
    private String certificateName;

    /**
     * 证书标准 1：TLS  2：SSL 3：其他
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "证书标准不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "证书标准提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "证书标准提交错误")
    @TableField("certificate_standard")
    private Integer certificateStandard;

    /**
     * 证书相关文件
     */
    @TableField("ca_file")
    private String caFile;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;
}
