package org.bem.iot.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.ProductAttrIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 档案属性
 * @author JiangShiYi
 */
@Data
@TableName("product_attr")
public class ProductAttr implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 属性ID
     */
    @NotNull(groups = Edit.class, message = "属性ID不能为空")
    @ProductAttrIdVerify(groups = Edit.class)
    @TableId(value = "attr_id", type = IdType.AUTO)
    private Long attrId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private String productId;

    /**
     * 分类路由
     */
    @TableField("class_route")
    private String classRoute;

    /**
     * 字段名称
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字段名称不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 30, message = "字段名称不能超过30个字符")
    @TableField("field_key")
    private String fieldKey;

    /**
     * 字段标题
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字段标题不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "字段标题不能超过20个字符")
    @TableField("field_label")
    private String fieldLabel;

    /**
     * 字段类型 input：输入框 text：文本框 date：日期 img：上传图片
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字段类型不能为空")
    @TableField("field_type")
    private String fieldType;

    /**
     * 字段描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联产品
     */
    @TableField(exist = false)
    private Product product;
}