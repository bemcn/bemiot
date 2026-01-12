package org.bem.iot.model.monitor;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务数据统计
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.statistics_business_data")
public class StatisticsBusinessData implements Serializable {
    @Serial
    private static final long serialVersionUID = -970953378826486842L;

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
     * 物模型数据量
     */
    @TableField("model_number")
    private Long modelNumber;

    /**
     * 日志数据量
     */
    @TableField("log_number")
    private Long logNumber;

    /**
     * 统计信息据量
     */
    @TableField("statistics_number")
    private Long statisticsNumber;

    /**
     * 消息数据量
     */
    @TableField("message_number")
    private Long messageNumber;

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
