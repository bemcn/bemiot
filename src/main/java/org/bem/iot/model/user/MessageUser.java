package org.bem.iot.model.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户内部消息
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.message_user")
public class MessageUser implements Serializable {
    @Serial
    private static final long serialVersionUID = -1180200328116561221L;

    /**
     * 时间戳
     */
    @ExcelProperty(value="创建时间", index=0)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * ID
     */
    @TableId("msg_id")
    private String msgId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("message")
    private String message;

    /**
     * 状态 0：未读 1：已读
     */
    @TableField("status")
    private Integer status;

    /**
     * 消息时间
     */
    @TableField("message_time")
    private Long messageTime;
}
