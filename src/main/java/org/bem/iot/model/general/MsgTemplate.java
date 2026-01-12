package org.bem.iot.model.general;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.bem.iot.validate.TemplateIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息模板
 * @author JiangShiYi
 */
@Data
@TableName("msg_template")
public class MsgTemplate implements Serializable {
    @Serial
    private static final long serialVersionUID = -2600517830335712330L;

    /**
     * 模板ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @TemplateIdVerify(groups = Edit.class)
    @TableId(value = "template_id", type = IdType.AUTO)
    private Long templateId;

    /**
     * 运用类型<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  wechat：微信消息模板<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  sms：短信消息模板<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  mail：邮件消息模板
     */
    @NotEmpty(groups = { Add.class }, message = "运用类型不能为空")
    @TableField("scope_app")
    private String scopeApp;

    /**
     * 模板标识
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "模板标识不能为空")
    @TableField("identity")
    private String identity;

    /**
     * 模板标题
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "模板标题不能为空")
    @TableField("title")
    private String title;

    /**
     * 模板内容
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "模板内容不能为空")
    @TableField("content")
    private String content;

    /**
     * 模板内容示例
     */
    @TableField("example")
    private String example;

    /**
     * 模版类型<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  微信：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  2：为一次性订阅<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  3：为长期订阅<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  短信：<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  0：短信通知<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  1：推广短信<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  2：验证码短信<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  6：国际/港澳台短信<br/>
     */
    @NotNull(groups = { Add.class, Edit.class }, message = "模版类型不能为空")
    @TableField("subtype")
    private Integer subtype;

    /**
     * 模板数据 - key-value数组
     *
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "模板数据不能为空")
    @TableField("enum_values")
    private String enumValues;

    /**
     * 模板关联签名
     */
    @TableField("sign_name")
    private String signName;

    /**
     * 数据来源<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  device：设备信息<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  model：物模型信息<br/>
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "数据来源不能为空")
    @TableField("data_source")
    private String dataSource;

    /**
     * 推送用户<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  all：所有用户<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  owner：设备所有者<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;  specified：指定用户<br/>
     */
    @NotEmpty(groups = { Add.class }, message = "推送用户不能为空")
    @TableField("user_source")
    private String userSource;

    /**
     * 指定用户
     */
    @TableField("user_data")
    private String userData;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
