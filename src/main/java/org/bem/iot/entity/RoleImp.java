package org.bem.iot.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户权限导入
 * @author JiangShiYi
 *
 */
@Data
public class RoleImp implements Serializable {
    @Serial
    private static final long serialVersionUID = -7904660141742690870L;

    @ExcelProperty("角色名称")
    private String roleName;

    @ExcelProperty("备注")
    private String remark;
}
