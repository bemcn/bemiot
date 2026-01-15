package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.entity.ScheduleStatus;
import org.bem.iot.mapper.postgresql.ScheduleMapper;
import org.bem.iot.model.general.Schedule;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 定时任务
 * @author jakybland
 */
@Service
public class ScheduleService {
    @Resource
    ScheduleMapper scheduleMapper;

    /**
     * 统计定时任务数量
     * @param example 统计条件
     * @return 定时任务数量
     */
    public long count(QueryWrapper<Schedule> example) {
        return scheduleMapper.selectCount(example);
    }

    /**
     * 查询定时任务列表
     * @return 定时任务列表
     */
    public List<Schedule> select(QueryWrapper<Schedule> example) {
        return scheduleMapper.selectList(example);
    }

    /**
     * 分页查询定时任务列表
     * @param index 页码
     * @param size 每页数量
     * @return 定时任务列表
     */
    public IPage<Schedule> selectPage(QueryWrapper<Schedule> example, long index, long size) {
        Page<Schedule> page = new Page<>(index, size);
        return scheduleMapper.selectPage(page, example);
    }

    /**
     * 判断定时任务ID是否存在
     * @param scheduleId 定时任务ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotScheduleId(int scheduleId) {
        QueryWrapper<Schedule> example = new QueryWrapper<>();
        example.eq("schedule_id", scheduleId);
        return !scheduleMapper.exists(example);
    }

    /**
     * 查询定时任务
     * @param scheduleId 定时任务ID
     * @return 定时任务信息
     */
    public Schedule find(@Param("scheduleId") int scheduleId) {
        return scheduleMapper.selectById(scheduleId);
    }

    /**
     * 添加定时任务
     * @param record 定时任务信息
     */
    public void insert(Schedule record) {
        record.setScheduleId(null);
        Date now = new Date();
        record.setStatus(1);
        record.setAbnormalTime(now);
        record.setCreateTime(now);
        scheduleMapper.insert(record);
    }

    /**
     * 修改定时任务
     * @param record 定时任务信息
     */
    public Schedule update(@Param("record") Schedule record) {
        scheduleMapper.updateById(record);
        return record;
    }

    /**
     * 修改状态
     * @param scheduleStatus 提交数据
     */
    public Schedule updateStatus(@Param("scheduleStatus") ScheduleStatus scheduleStatus) {
        Schedule record = scheduleMapper.selectById(scheduleStatus.getStatus());
        int status = scheduleStatus.getStatus();
        record.setStatus(status);
        if(status == 3) {
            record.setAbnormal(scheduleStatus.getAbnormal());
            record.setAbnormalMsg(scheduleStatus.getAbnormalMsg());
            record.setAbnormalTime(new Date());
        } else if(status == 2) {
            record.setAbnormal("");
            record.setAbnormalMsg("");
        }
        scheduleMapper.updateById(record);
        return record;
    }

    /**
     * 删除定时任务 (删除前需验证是否关联产品)
     * @param scheduleId 定时任务ID
     * @return 删除数量
     */
    public int del(@Param("scheduleId") int scheduleId) {
        return scheduleMapper.deleteById(scheduleId);
    }

    /**
     * 批量删除定时任务 (删除前需验证关联产品)
     * @param idList 定时任务ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        return scheduleMapper.deleteBatchIds(idList);
    }
}
