package org.bem.iot.model.iotbase;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class VtableInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7805606950272792728L;

    /**
     * ID
     */
    private String uid;

    /**
     * 虚拟表表名
     */
    private String tableName;

    /**
     * 数据库
     */
    private String dbName;

    /**
     * 创建事件
     */
    private Date createTime;

    /**
     * 列数
     */
    private Integer columns;

    /**
     * 所属超级表
     */
    private String stableName;

    /**
     * 分组ID
     */
    private Integer vgroupId;

    /**
     * 生命周期 单位：天
     */
    private Integer ttl;

    /**
     * 描述
     */
    private String table_comment;
}
