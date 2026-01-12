package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.DriveCodeVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备驱动
 * @author JiangShiYi
 */
@Data
@TableName("drives")
public class Drive implements Serializable {
    @Serial
    private static final long serialVersionUID = 4081992224500160919L;

    /**
     * 驱动编号
     */
    @NotNull(groups = Edit.class, message = "驱动编号不能为空")
    @DriveCodeVerify(groups = Edit.class)
    @TableId(value = "drive_code", type = IdType.NONE)
    private String driveCode;

    /**
     * 驱动名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "驱动名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "驱动名称不能超过50个字符")
    @TableField("drive_name")
    private String driveName;

    /**
     * 驱动协议
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "驱动协议不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "驱动协议不能超过50个字符")
    @TableField("protocol_id")
    private Integer protocolId;

    /**
     * 协议名称
     */
    @TableField(exist = false)
    private String protocolName;

    /**
     * 驱动来源
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "驱动来源不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "驱动来源不能超过50个字符")
    @TableField("drive_source")
    private String driveSource;

    /**
     * 驱动版本
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "驱动版本不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "驱动版本不能超过20个字符")
    @TableField("version")
    private String version;

    /**
     * 驱动描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 驱动地址
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "请上传驱动包文件")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 255, message = "驱动地址不能超过255个字符")
    @TableField("package_url")
    private String packageUrl;

    /**
     * 驱动状态 1：停止 2：启动
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否内置协议 0：否 1：是
     */
    @TableField("default_drive")
    private Integer defaultDrive;

    /**
     * 发行日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField("release_time")
    private Date releaseTime;
}
