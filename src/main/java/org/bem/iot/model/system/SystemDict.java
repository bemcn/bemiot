package org.bem.iot.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.DictIdVerify;
import org.bem.iot.validate.DictTypeIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统字典
 * @author JiangShiYi
 */
@Data
@TableName("system_dict")
public class SystemDict implements Serializable {
    @Serial
    private static final long serialVersionUID = 8184568660271324532L;

    /**
     * 字典ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DictIdVerify(groups = Edit.class)
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Integer dictId;

    /**
     * 字典类型ID
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "字典类型id不能为空")
    @DictTypeIdVerify(groups = { Add.class, Edit.class })
    @TableField("dict_type_id")
    private Integer dictTypeId;

    /**
     * 字典标签
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字典标识不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 100, message = "字典标识不能超过100个字符")
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字典标识不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 100, message = "字典标识不能超过100个字符")
    @TableField("dict_value")
    private String dictValue;

    /**
     * 是否默认 0：否 1：是
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "是否默认不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 0, message = "是否默认提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 1, message = "是否默认提交错误")
    @TableField("is_default")
    private Integer isDefault;

    /**
     * 排序值
     */
    @TableField("order_num")
    private Integer orderNum;
}
