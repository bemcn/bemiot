package org.bem.iot.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 产品分类导入
 * @author JiangShiYi
 */
@Data
public class ProductClassImp implements Serializable {
    @Serial
    private static final long serialVersionUID = -7651346732572183199L;

    /**
     * 分类名称
     */
    @ExcelProperty("分类名称")
    private String className;

    /**
     * 级联分类名称
     */
    @ExcelProperty("上级分类")
    private String levelName;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;
}
