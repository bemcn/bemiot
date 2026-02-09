package org.bem.iot.mapper.tdengine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface IotDataMapper {
    /**
     * 查询数量
     * @param tableName 表名
     * @return 返回数量
     */
    @Select("select count(*) from bemcn.${tableName}")
    long queryCount(@Param("tableName") String tableName);

    @Select("select count(*) from bemcn.${tableName} ${ew.customSqlSegment}")
    long queryCountBy(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(max(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryMaxValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(min(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryMinValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(avg(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryAvgValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(sum(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object querySumValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(apercentile(${column}, #{percentile}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryApercentileValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example, @Param("percentile") Integer percentile);

    @Select("select coalesce(percentile(${column}, #{percentile}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryPercentileValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example, @Param("percentile") Integer percentile);

    @Select("select coalesce(stddev(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryStddevValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(spread(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object querySpreadValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(hyperloglog(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryHyperloglogValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(first(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryFirstValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select coalesce(last(${column}), 0) from bemcn.${tableName} ${ew.customSqlSegment}")
    Object queryLastValue(@Param("tableName") String tableName, @Param("column") String column, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select * from bemcn.${tableName}")
    List<Map<String, Object>> queryList(@Param("tableName") String tableName);

    @Select("select * from bemcn.${tableName} ${ew.customSqlSegment}")
    List<Map<String, Object>> queryListBy(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Select("select * from bemcn.${tableName} ${ew.customSqlSegment}")
    IPage<Map<String, Object>> queryListPage(@Param("tableName") String tableName, @Param("page") Page<Map<String, Object>> page, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> queryWrapper);

    @Select("select * from bemcn.${tableName} ${ew.customSqlSegment} limit 1")
    Map<String, Object> queryOne(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> queryWrapper);

    @Select("select first(*) from bemcn.${tableName} ${ew.customSqlSegment}")
    Map<String, Object> queryFirst(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> queryWrapper);

    @Select("select last(*) from bemcn.${tableName} ${ew.customSqlSegment}")
    Map<String, Object> queryLast(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> queryWrapper);

    @Delete("DELETE FROM bemcn.${tableName} WHERE ts=#{ts}")
    int delByTs(@Param("tableName") String tableName, @Param("ts") Timestamp ts);

    @Delete("DELETE FROM bemcn.${tableName} ${ew.customSqlSegment}")
    int delByCondition(@Param("tableName") String tableName, @Param(Constants.WRAPPER) QueryWrapper<Map<String, Object>> example);

    @Delete("DELETE FROM bemcn.${tableName}")
    int delAll(@Param("tableName") String tableName);

    @Insert("${sql}")
    void executeSql(String sql);
}
