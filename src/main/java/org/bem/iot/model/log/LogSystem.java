package org.bem.iot.model.log;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统日志
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.log_system")
public class LogSystem implements Serializable {
    @Serial
    private static final long serialVersionUID = 7605928595159080583L;

    /**
     * 时间戳
     */
    @ExcelProperty(value="创建时间", index=0)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * id
     */
    @ExcelIgnore
    @TableField("log_id")
    private String logId;

    /**
     * 客户端来源
     */
    @ExcelProperty(value="来源", index=3)
    @TableField("client_source")
    private String clientSource;

    /**
     * 客户端IP
     */
    @ExcelProperty(value="IP地址", index=4)
    @TableField("client_ip")
    private String clientIp;

    /**
     * 用户ID
     */
    @ExcelIgnore
    @TableField("user_id")
    private Integer userId;

    /**
     * 账号
     */
    @ExcelProperty(value="账号", index=1)
    @TableField("user_name")
    private String userName;

    /**
     * 昵称
     */
    @ExcelProperty(value="昵称", index=2)
    @TableField("nick_name")
    private String nickName;

    /**
     * 操作模块
     */
    @ExcelProperty(value="模块", index=5)
    @TableField("model_name")
    private String modelName;

    /**
     * 操作模式  新增、编辑、删除
     */
    @ExcelProperty(value="事件", index=6)
    @TableField("operation")
    private String operation;

    /**
     * 描述
     */
    @ExcelProperty(value="描述", index=7)
    @TableField("description")
    private String description;
}
