package org.bem.iot.model.iotbase;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TableInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2936473009393704070L;

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

    /**
     * 表类型<br/>
     * CHILD_TABLE：子表<br/>
     * SYSTEM_TABLE：系统表<br/>
     * NORMAL_TABLE：普通表 <br/>
     * VIRTUAL_CHILD_TABLE：虚拟子表 <br/>
     * VIRTUAL_NORMAL_TABLE：虚拟普通表
     */
    private String type;
}
