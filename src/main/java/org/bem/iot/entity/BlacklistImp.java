package org.bem.iot.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 黑名单导入
 * @author JiangShiYi
 */
@Data
public class BlacklistImp implements Serializable {
    @Serial
    private static final long serialVersionUID = 3649145777420168344L;

    /**
     * 开始ip
     */
    @ExcelProperty("开始IP")
    private String ipStart;

    /**
     * 截止IP
     */
    @ExcelProperty("截止IP")
    private String ipEnd;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @TableField("remark")
    private String remark;
}
