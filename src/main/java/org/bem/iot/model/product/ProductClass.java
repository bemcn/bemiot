package org.bem.iot.model.product;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.ProductClassIdVerify;
import org.bem.iot.validate.ProductClassLevelIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 产品分类
 * @author JiangShiYi
 */
@Data
@TableName("product_class")
public class ProductClass implements Serializable {
    @Serial
    private static final long serialVersionUID = 8002488364355857178L;

    /**
     * 分类ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @ProductClassIdVerify(groups = Edit.class)
    @ExcelProperty("ID")
    @TableId(value = "class_id", type = IdType.AUTO)
    private Integer classId;

    /**
     * 分类名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "分类名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "分类名称不能超过20个字符")
    @ExcelProperty("分类名称")
    @TableField("class_name")
    private String className;

    /**
     * 上级分类ID
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "上级分类id不能为空")
    @ProductClassLevelIdVerify(groups = { Add.class, Edit.class })
    @ExcelIgnore
    @TableField("level_id")
    private Integer levelId;

    /**
     * 上级分类
     */
    @ExcelProperty("上级分类")
    @TableField("level_name")
    private String levelName;

    /**
     * 排序值
     */
    @ExcelIgnore
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    @TableField("remark")
    private String remark;

    /**
     * 关联驱动
     */
    @TableField(exist = false)
    private long childCount;
}
