package org.bem.iot.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户导入数据
 * @author JiangShiYi
 *
 */
@Data
public class UserImp implements Serializable {
    @Serial
    private static final long serialVersionUID = -4835385464035010134L;

    /**
     * 账号
     */
    @ExcelProperty("账号")
    private String userName;

    /**
     * 昵称
     */
    @ExcelProperty("昵称")
    private String nickName;

    /**
     * 角色名称
     */
    @ExcelProperty("角色")
    private String roleName;

    /**
     * 性别 男/女
     */
    @ExcelProperty("性别")
    private String sex;

    /**
     * 手机号码
     */
    @ExcelProperty("手机号码")
    private String phone;

    /**
     * 电子邮箱
     */
    @ExcelProperty("电子邮箱")
    private String email;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;
}
