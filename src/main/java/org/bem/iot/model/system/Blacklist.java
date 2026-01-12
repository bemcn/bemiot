package org.bem.iot.model.system;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.BlacklistIdVerify;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 黑名单
 * @author JiangShiYi
 */
@Data
@TableName("blacklist")
public class Blacklist implements Serializable {
    @Serial
    private static final long serialVersionUID = -4763908778014038098L;

    /**
     * ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @BlacklistIdVerify(groups = Edit.class)
    @ExcelProperty("ID")
    @TableId(value = "black_id", type = IdType.AUTO)
    private Long blackId;

    /**
     * 开始IP
     */
    @NotNull(groups = Edit.class, message = "开始IP不能为空")
    @ExcelProperty("开始IP")
    @TableField("ip_start")
    private String ipStart;

    /**
     * 截止IP
     */
    @ExcelProperty("截止IP")
    @TableField("ip_end")
    private String ipEnd;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelIgnore
    @TableField("create_time")
    private Date createTime;
}
