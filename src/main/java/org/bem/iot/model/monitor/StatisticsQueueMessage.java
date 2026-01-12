package org.bem.iot.model.monitor;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息队列数据统计
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.statistics_queue_message")
public class StatisticsQueueMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 6472297523010488142L;

    /**
     * 时间戳
     */
    @ExcelProperty(value="创建时间", index=0)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Long ts;

    /**
     * ID
     */
    @TableField("log_id")
    private String logId;

    /**
     * 声明数量
     */
    @TableField("declared_number")
    private Long declaredNumber;

    /**
     * 创建数量
     */
    @TableField("created_number")
    private Long createdNumber;

    /**
     * 删除数量
     */
    @TableField("deleted_number")
    private Long deletedNumber;

    /**
     * 年
     */
    @TableField("year")
    private Integer year;

    /**
     * 月
     */
    @TableField("month")
    private Integer month;

    /**
     * 日
     */
    @TableField("day")
    private Integer day;

    /**
     * 时
     */
    @TableField("hour")
    private Integer hour;
}
