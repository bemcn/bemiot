package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.tdengine.LogScheduleTaskMapper;
import org.bem.iot.model.log.LogScheduleTask;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务日志
 * @author jakybland
 */
@Service
public class LogScheduleTaskService {
    @Resource
    LogScheduleTaskMapper logScheduleTaskMapper;

    /**
     * 获取统计数量
     * @param example 查询条件
     * @return 返回数量值
     */
    public long count(QueryWrapper<LogScheduleTask> example) {
        return logScheduleTaskMapper.selectCount(example);
    }

    /**
     * 查询指定数量的数据
     * @param example 查询条件
     * @param size 显示数量
     * @return 符合条件的数据列表
     */
    public List<LogScheduleTask> selectLimit(QueryWrapper<LogScheduleTask> example, long size) {
        IPage<LogScheduleTask> page = new Page<>(1L, size);
        IPage<LogScheduleTask> result = logScheduleTaskMapper.selectPage(page, example);
        return result.getRecords();
    }

    /**
     * 分页查询数据
     * @param example 查询条件
     * @param index 查询的页码
     * @param size 每页数量
     * @return 返回查询结果
     */
    public IPage<LogScheduleTask> selectPage(QueryWrapper<LogScheduleTask> example, long index, long size) {
        Page<LogScheduleTask> page = new Page<>(index, size);
        return logScheduleTaskMapper.selectPage(page, example);
    }

    /**
     * 插入新的数据
     * @param scheduleName 任务名称
     * @param description 任务描述
     * @param runType 执行方式
     * @param status 状态 1：成功 2：失败
     */
    public void insert(String scheduleName,  String description, String runType, int status) {
        String id = InternalIdUtil.createId();
        LogScheduleTask record = new LogScheduleTask();
        record.setTs(System.currentTimeMillis());
        record.setLogId(id);
        record.setScheduleName(scheduleName);
        record.setDescription(description);
        record.setRunType(runType);
        record.setStatus(status);
        logScheduleTaskMapper.insert(record);
    }

    /**
     * 删除数据
     * @param id 删除的ID
     */
    public void del(@Param("id") String id) {
        QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
        example.eq("log_id", id);
        logScheduleTaskMapper.delete(example);
    }

    /**
     * 批量删除数据
     * @param idList 删除的ID列表
     */
    public void delArray(List<String> idList) {
        QueryWrapper<LogScheduleTask> example = new QueryWrapper<>();
        example.in("log_id", idList);
        logScheduleTaskMapper.delete(example);
    }
}
