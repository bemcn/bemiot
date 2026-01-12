package org.bem.iot.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.model.general.Drive;
import org.bem.iot.model.general.Firmware;
import org.bem.iot.validate.*;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品信息
 * @author JiangShiYi
 */
@Data
@TableName("product")
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 6169400148566621761L;

    /**
     * 产品ID
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "id不能为空")
    @ProductIdAddVerify(groups = Add.class)
    @ProductIdVerify(groups = Edit.class)
    @TableId(value = "product_id", type = IdType.NONE)
    private String productId;

    /**
     * 产品名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "产品名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "产品名称不能超过20个字符")
    @TableField("product_name")
    private String productName;

    /**
     * 分类ID
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "分类Iid不能为空")
    @ProductClassIdVerify(groups = { Add.class, Edit.class })
    @TableField("class_id")
    private Integer classId;

    /**
     * 分类路由
     */
    @TableField("class_route")
    private String classRoute;

    /**
     * 产品型号
     */
    @TableField("models")
    private String models;

    /**
     * 产品图片
     */
    @TableField("images")
    private String images;

    /**
     * 产品属性
     */
    @TableField("attribute")
    private String attribute;

    /**
     * 产品类型 1：直连设备 2：网关设备 3：监控设备 4：视频存储设备 5：网关子设备 6：虚拟设备
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "产品类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "产品类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 6, message = "产品类型提交错误")
    @TableField("types")
    private Integer types;

    /**
     * 生产厂商
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 供应商
     */
    @TableField("supplier")
    private String supplier;

    /**
     * 产品固件ID 0：无
     */
    @FirmwareIdVerify(groups = { Add.class, Edit.class })
    @TableField("firmware_id")
    private Integer firmwareId;

    /**
     * 驱动编号
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "驱动编号不能为空")
    @DriveCodeVerify(groups = { Add.class, Edit.class })
    @TableField("drive_code")
    private String driveCode;

    /**
     * 传输协议(字典):
     * <br>bem-json:Bem Json解析协议
     * <br>bem-net:Bem TCP/UDP协议
     * <br>bem-modbus-rtu:Bem Modbus RTU协议
     * <br>bem-modbus-tcp:Bem Modbus TCP协议
     * <br>...其他驱动包自定义协议
     */
    @TableField("trans")
    private String trans;

    /**
     * 协议采集类型:
     * <br>0:设备主动上传
     * <br>1:网关主动采集
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "协议采集类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "协议采集类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "协议采集类型提交错误")
    @TableField("collect_type")
    private Integer collectType;

    /**
     * 认证方式 0：无 1：简单认证 2：加密认证 3：简单+加密
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "认证方式不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "认证方式提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "认证方式提交错误")
    @TableField("auth_method")
    private Integer authMethod;

    /**
     * 联网方式 1：以太网 2：Wifi 3：蜂窝 4：NB-IOT 5：串口通讯 6：其他
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "联网方式不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "联网方式提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 6, message = "联网方式提交错误")
    @TableField("net_method")
    private Integer netMethod;

    /**
     * 定位方式 0：无 1：自动定位 2：设备定位 3：自定义位置
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "定位方式不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "定位方式提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 3, message = "定位方式提交错误")
    @TableField("locate_method")
    private Integer locateMethod;

    /**
     * 设备授权 0：未启用 1：已启用
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "设备授权不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "设备授权提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "设备授权提交错误")
    @TableField("auth_equipment")
    private Integer authEquipment;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 1：待发布，2：已发布
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 关联分类
     */
    @TableField(exist = false)
    private ProductClass productClass;

    /**
     * 关联固件
     */
    @TableField(exist = false)
    private Firmware firmware;

    /**
     * 关联驱动
     */
    @TableField(exist = false)
    private Drive drive;
}
