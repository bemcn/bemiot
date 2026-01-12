package org.bem.iot.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bem.iot.validate.DictTypeIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统字典类型
 * @author JiangShiYi
 */
@Data
@TableName("system_dict_type")
public class SystemDictType implements Serializable {
    @Serial
    private static final long serialVersionUID = 5885881705421012430L;

    /**
     * 字典类型ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @DictTypeIdVerify(groups = Edit.class)
    @TableId(value = "dict_type_id", type = IdType.AUTO)
    private Integer dictTypeId;

    /**
     * 字典标识
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字典标识不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 50, message = "字典标识不能超过50个字符")
    @TableField("type_key")
    private String typeKey;

    /**
     * 字典类型
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "字典类型不能为空")
    @Size(groups = { Add.class, Edit.class }, min = 1, max = 20, message = "字典类型不能超过20个字符")
    @TableField("type_name")
    private String typeName;
}
