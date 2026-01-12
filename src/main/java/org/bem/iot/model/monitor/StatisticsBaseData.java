package org.bem.iot.model.monitor;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础数据统计
 * @author JiangShiYi
 */
@Data
@TableName("bemcn.statistics_base_data")
public class StatisticsBaseData implements Serializable {
    @Serial
    private static final long serialVersionUID = -4078268292126007242L;

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
     * 用户数量
     */
    @TableField("user_number")
    private Long userNumber;

    /**
     * 产品数量
     */
    @TableField("product_number")
    private Long productNumber;

    /**
     * 设备数量
     */
    @TableField("device_number")
    private Long deviceNumber;

    /**
     * 物模型数量
     */
    @TableField("model_number")
    private Long modelNumber;

    /**
     * 视频通道数量
     */
    @TableField("channel_number")
    private Long channelNumber;

    /**
     * 数据桥接规则
     */
    @TableField("bridging_number")
    private Long bridgingNumber;

    /**
     * 设备联动规则
     */
    @TableField("linkage_number")
    private Long linkageNumber;

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
