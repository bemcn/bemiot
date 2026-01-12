package org.bem.iot.model.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.DeviceIdVerify;
import org.bem.iot.validate.DeviceUserIdVerify;
import org.bem.iot.validate.DeviceUserVerify;
import org.bem.iot.validate.UserIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 设备用户权限
 * @author JiangShiYi
 */
@Data
@TableName("device_user")
public class DeviceUser implements Serializable {
    @Serial
    private static final long serialVersionUID = -7394573999281774310L;

    /**
     * id
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DeviceUserIdVerify(groups = Edit.class)
    @TableId(value = "device_user_id", type = IdType.AUTO)
    private Long deviceUserId;

    /**
     * 设备ID
     */
    @NotNull(groups = Add.class, message = "设备id不能为空")
    @DeviceIdVerify(groups = Add.class)
    @TableField("device_id")
    private String deviceId;

    /**
     * 用户ID
     */
    @NotNull(groups = Add.class, message = "用户id不能为空")
    @UserIdVerify(groups = Add.class)
    @DeviceUserVerify(groups = Add.class)
    @TableField("user_id")
    private Integer userId;

    /**
     * 是否为设备所有者 0：否  1：是
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "是否为设备所有者不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "是否为设备所有者提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "是否为设备所有者提交错误")
    @TableField("is_owner")
    private Integer isOwner;

    /**
     * 物模型权限
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "物模型权限不能为空")
    @TableField("user_role")
    private String userRole;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 设备ID
     */
    @TableField(exist = false)
    private Device device;

    /**
     * 用户
     */
    @TableField(exist = false)
    private Map<String, Object> user;
}
