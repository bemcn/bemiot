package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceControlsMapper;
import org.bem.iot.model.device.DeviceControls;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备群控
 * @author jakybland
 */
@Service
public class DeviceControlsService {
    @Resource
    DeviceControlsMapper deviceControlsMapper;

    /**
     * 统计设备群控数量
     * @param example 统计条件
     * @return 设备群控数量
     */
    public long count(QueryWrapper<DeviceControls> example) {
        return deviceControlsMapper.selectCount(example);
    }

    /**
     * 分页查询设备群控列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 设备群控列表
     */
    public IPage<DeviceControls> selectPage(QueryWrapper<DeviceControls> example, long index, long size) {
        Page<DeviceControls> page = new Page<>(index, size);
        return deviceControlsMapper.selectPage(page, example);
    }

    /**
     * 判断设备群控ID是否存在
     * @param controlId 设备群控ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotControlId(long controlId) {
        QueryWrapper<DeviceControls> example = new QueryWrapper<>();
        example.eq("control_id", controlId);
        return !deviceControlsMapper.exists(example);
    }

    /**
     * 查询设备群控
     * @param controlId 设备群控ID
     * @return 设备群控信息
     */
    public DeviceControls find(@Param("controlId") long controlId) {
        return deviceControlsMapper.selectById(controlId);
    }

    /**
     * 添加设备群控
     * @param record 设备群控信息
     */
    public void insert(DeviceControls record) {
        record.setControlId(null);
        deviceControlsMapper.insert(record);
    }

    /**
     * 修改设备群控
     * @param record 设备群控信息
     */
    public DeviceControls update(@Param("record") DeviceControls record) {
        deviceControlsMapper.updateById(record);
        return record;
    }

    /**
     * 删除设备群控
     * @param controlId 设备群控ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("controlId") long controlId) throws Exception {
        return deviceControlsMapper.deleteById(controlId);
    }

    /**
     * 批量删除设备群控
     * @param idList 设备群控ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<Long> idList) throws Exception {
        return deviceControlsMapper.deleteBatchIds(idList);
    }
}
