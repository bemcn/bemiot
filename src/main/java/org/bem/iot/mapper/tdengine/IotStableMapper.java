package org.bem.iot.mapper.tdengine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.bem.iot.model.iotbase.Stable;
import org.bem.iot.model.iotbase.TableInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface IotStableMapper {
    /**
     * 创建超级表
     * @param tableName 超级表表名
     * @param columns 列集合
     * @param tags tag集合
     * @param notes 描述
     */
    @Update("CREATE STABLE IF NOT EXISTS bemcn.${tableName} (${columns}) TAGS (${tags}) COMMENT '${notes}'")
    void createStable(@Param("tableName") String tableName, @Param("columns") String columns, @Param("tags") String tags, @Param("notes") String notes);

    /**
     * 创建虚拟超级表
     * @param tableName 虚拟超级表表名
     * @param columns 列集合
     * @param tags tag集合
     * @param notes 描述
     */
    @Update("CREATE STABLE IF NOT EXISTS bemcn.${tableName} (${columns}) TAGS (${tags}) COMMENT '${notes}' VIRTUAL 1")
    void createVirtualStable(@Param("tableName") String tableName, @Param("columns") String columns, @Param("tags") String tags, @Param("notes") String notes);


    /**
     * 创建子表
     * @param tableName 子表表名
     * @param subTableName 子表表名
     * @param tagData tag数据
     * @param notes 描述
     */
    @Update("CREATE TABLE IF NOT EXISTS bemcn.${subTableName} USING bemcn.${tableName} TAGS (${tagData}) COMMENT '${notes}'")
    void createSubTable(@Param("tableName") String tableName, @Param("subTableName") String subTableName, @Param("tagData") String tagData, @Param("notes") String notes);

    /**
     * 创建虚拟子表
     * @param tableName 虚拟子表表名
     * @param subTableName 子表表名
     * @param columns 列集合（如果是绑定列，则c1 int from bemcn.t1.value 或 null）
     * @param tagData tag数据
     * @param notes 描述
     */
    @Update("CREATE VTABLE IF NOT EXISTS bemcn.${subTableName} (${columns}) USING bemcn.${tableName} TAGS (${tagData}) COMMENT '${notes}'")
    void createVirtualSubTable(@Param("tableName") String tableName, @Param("subTableName") String subTableName, @Param("columns") String columns, @Param("tagData") String tagData, @Param("notes") String notes);

    /**
     * 创建普通表
     * @param tableName 普通表表名
     * @param columns 列集合
     */
    @Update("CREATE TABLE IF NOT EXISTS bemcn.${tableName} (${columns}) COMMENT '${notes}'")
    void createTable(@Param("tableName") String tableName, @Param("columns") String columns, @Param("notes") String notes);

    /**
     * 创建虚拟普通表
     * @param tableName 虚拟普通表表名
     * @param columns 列集合（（如果是绑定列，则c1 int from bemcn.t1.value 或 null）
     */
    @Update("CREATE VTABLE IF NOT EXISTS bemcn.${tableName} (${columns}) COMMENT '${notes}'")
    void createVirtualTable(@Param("tableName") String tableName, @Param("columns") String columns, @Param("notes") String notes);

    /**
     * 查询超级表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.STABLES")
    List<String> selectStables();

    /**
     * 查询超级表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.STABLES LIKE '${tableName}'")
    List<String> selectStablesLike(@Param("tableName") String tableName);

    /**
     * 查询普通表名或子表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.TABLES")
    List<String> selectTables();

    /**
     * 查询普通表名或子表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.TABLES LIKE '${tableName}'")
    List<String> selectTablesLike(@Param("tableName") String tableName);

    /**
     * 查询普通表名或子表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.TABLES LIKE '${tableName}%'")
    List<String> selectTablesLikeLift(@Param("tableName") String tableName);

    /**
     * 查询普通表名或子表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.TABLES LIKE '%${tableName}%'")
    List<String> selectTablesLikeContent(@Param("tableName") String tableName);

    /**
     * 查询普通表名或子表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.TABLES LIKE '%${tableName}'")
    List<String> selectTablesLikeRight(@Param("tableName") String tableName);

    /**
     * 查询虚拟普通表名或虚拟子表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.VTABLES")
    List<String> selectVirtualTables();

    /**
     * 查询虚拟普通表名或虚拟子表名
     * @param tableName 表名
     * @return 返回表名集合,无则返回null
     */
    @Select("SHOW bemcn.VTABLES LIKE '${tableName}'")
    List<String> selectVirtualTablesLike(@Param("tableName") String tableName);

    /**
     * 获取超级表的创建语句
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("SHOW CREATE STABLE bemcn.${tableName}")
    Map<String, String> selectStableCreateSql(@Param("tableName") String tableName);

    /**
     * 获取普通表或子表的创建语句
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("SHOW CREATE TABLE bemcn.${tableName}")
    Map<String, String> selectTableCreateSql(@Param("tableName") String tableName);

    /**
     * 获取虚拟普通表或虚拟子表的创建语句
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("SHOW CREATE VTABLE bemcn.${tableName}")
    Map<String, String> selectVirtualTableCreateSql(@Param("tableName") String tableName);

    /**
     * 获取表的结构信息(超级表、普通表、子表、虚拟普通表、虚拟子表)
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("DESCRIBE bemcn.${tableName}")
    List<Stable> selectTableStruct(@Param("tableName") String tableName);

    /**
     * 查看所有表信息
     * @return 返回数据列表
     */
    @Select("SELECT * FROM information_schema.ins_tables ${ew.customSqlSegment}")
    List<TableInfo> selectTablesInfo(@Param(Constants.WRAPPER) QueryWrapper<TableInfo> example);

    /**
     * 查看所有虚拟表信息
     * @return 返回数据列表
     */
    @Select("SHOW CREATE VTABLE bemcn.${tableName}")
    List<Stable> selectVirtualTablesInfo(@Param("tableName") String tableName);

    /**
     * 获取表中所有子表的标签信息(超级表、普通表、子表等)
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("SHOW TABLE TAGS FROM bemcn.${tableName}")
    List<Map<String, Object>> selectTableTags(@Param("tableName") String tableName);

    /**
     * 获取表中指定标签列的值(超级表、普通表、子表等)
     * @param tableName 表名
     * @return 返回 Table和Create Table字段的map;失败抛出错误
     */
    @Select("SELECT DISTINCT ${columns} FROM bemcn.${tableName}")
    List<Map<String, Object>> selectTableSpecifyTags(@Param("tableName") String tableName, @Param("columns") String columns);

    /**
     * 超级表添加列
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} ADD COLUMN ${columnName} ${columnType}")
    int addStableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);

    /**
     * 超级表添加列
     * @param tableName 表名
     * @param columnName 列名
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} DROP COLUMN ${columnName}")
    int dropStableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 超级表修改列的宽度(数据列的类型必须是 nchar 和 binary，使用此指令可以修改其宽度，只能改大，不能改小)
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} MODIFY COLUMN ${columnName} ${columnType}")
    int modifyStableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);

    /**
     * 超级表添加一个标签
     * @param tableName 表名
     * @param tagName tag名
     * @param tagType tag数据类型
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} ADD TAG ${tagName} ${tagType}")
    int addStableTag(@Param("tableName") String tableName, @Param("columnName") String tagName, @Param("tagType") String tagType);

    /**
     * 超级表删除一个标签(子表也会删除)
     * @param tableName 表名
     * @param tagName tag名
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} DROP TAG ${tagName}")
    int dropStableTag(@Param("tableName") String tableName, @Param("columnName") String tagName);

    /**
     * 超级表修改标签宽度(数据列的类型必须是 nchar 和 binary，使用此指令可以修改其宽度，只能改大，不能改小)
     * @param tableName 表名
     * @param tagName tag名
     * @param tagType tag数据类型
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} MODIFY TAG ${tagName} ${tagType}")
    int modifyStableTag(@Param("tableName") String tableName, @Param("tagName") String tagName, @Param("tagType") String tagType);

    /**
     * 超级表修改标签名称
     * @param tableName 表名
     * @param oldTagName 原tag名
     * @param newTagName 新tag名
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} RENAME TAG ${oldTagName} ${newTagName}")
    int renameStableTag(@Param("tableName") String tableName, @Param("oldTagName") String oldTagName, @Param("newTagName") String newTagName);

    /**
     * 超级表修改注释
     * @param tableName 表名
     * @param notes 注释
     * @return 返回0
     */
    @Update("ALTER STABLE bemcn.${tableName} COMMENT \"${notes}\"")
    int modifyStableNotes(@Param("tableName") String tableName, @Param("columnName") String notes);

    /**
     * 普通表添加列
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} ADD COLUMN ${columnName} ${columnType}")
    int addTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);

    /**
     * 普通表删除列
     * @param tableName 表名
     * @param columnName 列名
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} DROP COLUMN ${columnName}")
    int dropTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 普通表修改列的宽度(数据列的类型必须是 nchar 和 binary，使用此指令可以修改其宽度，只能改大，不能改小)
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} MODIFY COLUMN ${columnName} ${columnType}")
    int modifyTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);

    /**
     * 普通表修改列名
     * @param tableName 表名
     * @param oldColumnName 原列名
     * @param newColumnName 新列名
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} RENAME COLUMN ${oldColumnName} ${newColumnName}")
    int renameTableColumn(@Param("tableName") String tableName, @Param("oldColumnName") String oldColumnName, @Param("newColumnName") String newColumnName);

    /**
     * 子表添修改标签值
     * @param tableName 表名
     * @param columnValues 标签修改字段 格式：标签名1=值1,标签名2=值2
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} SET TAG ${columnValues}")
    int modifySubTableTag(@Param("tableName") String tableName, @Param("columnValues") String columnValues);

    /**
     * 普通表、子表修改生命周期
     * @param tableName 表名
     * @param value 天数 0-2147483647 TTL单位是天
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} TTL ${value}")
    int modifyTableTTL(@Param("tableName") String tableName, @Param("value") int value);

    /**
     * 普通表、子表修改注释
     * @param tableName 表名
     * @param notes 注释
     * @return 返回0
     */
    @Update("ALTER TABLE bemcn.${tableName} COMMENT '${notes}'")
    int modifyTableNotes(@Param("tableName") String tableName, @Param("columnName") String notes);




    /**
     * 虚拟普通表添加列（非绑定列）
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} ADD COLUMN ${columnName} ${columnType}")
    int addVirtualTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);


    /**
     * 虚拟普通表添加列（绑定列）
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @param fromColumn 绑定指定表的列名
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} ADD COLUMN ${columnName} ${columnType} ${fromColumn}")
    int addVirtualTableColumnWithBinding(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType, @Param("fromColumn") String fromColumn);

    /**
     * 虚拟普通表删除列
     * @param tableName 表名
     * @param columnName 列名
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} DROP COLUMN ${columnName}")
    int dropVirtualTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 虚拟普通表修改列的宽度(数据列的类型必须是 nchar 和 binary，使用此指令可以修改其宽度，只能改大，不能改小)
     * @param tableName 表名
     * @param columnName 列名
     * @param columnType 列数据类型
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} MODIFY COLUMN ${columnName} ${columnType}")
    int modifyVirtualTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("columnType") String columnType);

    /**
     * 普通表修改列名
     * @param tableName 表名
     * @param oldColumnName 原列名
     * @param newColumnName 新列名
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} RENAME COLUMN ${oldColumnName} ${newColumnName}")
    int renameVirtualTableColumn(@Param("tableName") String tableName, @Param("oldColumnName") String oldColumnName, @Param("newColumnName") String newColumnName);

    /**
     * 普通表修改数据源（绑定）
     * @param tableName 表名
     * @param columnName 列名
     * @param fromColumn 绑定列名（格式：c1 int from bemcn.t1.value 或 null）
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} ALTER COLUMN ${columnName} SET ${fromColumn}")
    int sourceVirtualTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("fromColumn") String fromColumn);

    /**
     * 虚拟子表修改Tag标签
     * @param tableName 表名
     * @param columnValues 标签修改字段 格式：标签名1=值1,标签名2=值2
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} SET TAG ${columnName} SET ${columnValues}")
    int modifyVirtualSubTableTag(@Param("tableName") String tableName, @Param("columnValues") String columnValues);

    /**
     * 虚拟子表修修改数据源（绑定）
     * @param tableName 表名
     * @param columnName 列名
     * @param fromColumn 绑定列名（格式：c1 int from bemcn.t1.value 或 null）
     * @return 返回0
     */
    @Update("ALTER VTABLE bemcn.${tableName} ALTER COLUMN ${columnName} SET ${fromColumn}")
    int sourceVirtualSubTableColumn(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("fromColumn") String fromColumn);

    /**
     * 删除超级表
     * @param tableName 表名
     */
    @Update("DROP STABLE IF EXISTS bemcn.${tableName}")
    void dropStable(@Param("tableName") String tableName);

    /**
     * 删除子表、普通表
     * @param subTableName 子表名
     */
    @Update("DROP TABLE IF EXISTS bemcn.${subTableName}")
    void delSubTable(@Param("subTableName") String subTableName);

    /**
     * 删除多张子表、普通表
     * @param tables 子表名名集合
     */
    @Update("DROP TABLE ${tables}")
    void delSubTableArray(@Param("tables") String tables);

    /**
     * 删除虚拟普通表、虚拟子表
     * @param subTableName 子表名
     */
    @Update("DROP VTABLE IF EXISTS bemcn.${subTableName}")
    void delVirtualTable(@Param("subTableName") String subTableName);
}
