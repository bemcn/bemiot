package org.bem.iot.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bem.iot.validate.NoticeIdVerify;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 通知公告
 * @author [Your Name]
 */
@Data
@TableName("system_notice")
public class SystemNotice implements Serializable {
    @Serial
    private static final long serialVersionUID = -7661573115058917463L;

    /**
     * 公告ID
     */
    @NotNull(groups = Edit.class, message = "id不能为空")
    @NoticeIdVerify(groups = Edit.class)
    @TableId(value = "notice_id", type = IdType.NONE)
    private Integer noticeId;

    /**
     * 标题
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "标题不能为空")
    @TableField("notice_title")
    private String noticeTitle;

    /**
     * 公告类型 1：通知 2：公告
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "公告类型不能为空")
    @Min(groups = { Add.class, Edit.class }, value = 1, message = "公告类型提交错误")
    @Max(groups = { Add.class, Edit.class }, value = 2, message = "公告类型提交错误")
    @TableField("notice_type")
    private Integer noticeType;

    /**
     * 公告内容
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "公告内容不能为空")
    @TableField("notice_content")
    private String noticeContent;

    /**
     * 用户ID
     */
    @NotEmpty(groups = { Add.class, Edit.class }, message = "用户ID不能为空")
    @TableField("user_id")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    @TableField("user")
    private Map<String, Object> user;
}
