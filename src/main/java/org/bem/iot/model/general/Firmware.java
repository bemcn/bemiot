package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.FirmwareIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 产品固件
 * @author JiangShiYi
 */
@Data
@TableName("firmware")
public class Firmware implements Serializable {
    @Serial
    private static final long serialVersionUID = 657897926041701483L;

    /**
     * 固件ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @FirmwareIdVerify(groups = Edit.class)
    @TableId(value = "firmware_id", type = IdType.AUTO)
    private Integer firmwareId;

    /**
     * 固件名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "固件名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "固件名称不能超过50个字符")
    @TableField("firmware_name")
    private String firmwareName;

    /**
     * 最新版本号
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "版本号不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "版本号不能超过20个字符")
    @TableField("version")
    private String version;

    /**
     * 更新方式 1：http 2：分包拉取
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "固件类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "固件类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 2, message = "固件类型提交错误")
    @TableField("install_type")
    private Integer installType;

    /**
     * 固件包上传地址
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "固件包地址不能为空")
    @TableField(exist = false)
    private String url;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;
}
