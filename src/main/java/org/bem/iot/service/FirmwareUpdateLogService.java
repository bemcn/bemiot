package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.FirmwareUpdateLogMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.general.FirmwareUpdateLog;
import org.bem.iot.model.general.FirmwareUpdateTask;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 固件升级日志
 * @author jakybland
 */
@Service
public class FirmwareUpdateLogService {
    @Resource
    FirmwareUpdateLogMapper firmwareUpdateLogMapper;

    @Resource
    FirmwareUpdateTaskService firmwareUpdateTaskService;

    @Resource
    DeviceService deviceService;

    /**
     * 分页查询固件升级统计
     * @param firmwareId 查询条件
     * @return 固件列表
     */
    public JSONObject selectTotal(int firmwareId) {
        QueryWrapper<FirmwareUpdateLog> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        long total = firmwareUpdateLogMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        example.eq("status", 3);
        long succeed = firmwareUpdateLogMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        example.lt("status", 3);
        long update = firmwareUpdateLogMapper.selectCount(example);

        example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        example.gt("status", 3);
        long fail = firmwareUpdateLogMapper.selectCount(example);

        JSONObject obj = new JSONObject();
        obj.put("total", total);
        obj.put("succeed", succeed);
        obj.put("update", update);
        obj.put("fail", fail);
        return obj;
    }


    /**
     * 分页查询固件升级计划列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 固件列表
     */
    public IPage<FirmwareUpdateLog> selectPage(QueryWrapper<FirmwareUpdateLog> example, long index, long size) {
        Page<FirmwareUpdateLog> page = new Page<>(index, size);
        IPage<FirmwareUpdateLog> ipage = firmwareUpdateLogMapper.selectPage(page, example);
        List<FirmwareUpdateLog> list = ipage.getRecords();
        for(int i = 0; i < list.size(); i++) {
            FirmwareUpdateLog log = list.get(i);
            int taskId = log.getTaskId();
            String deviceId = log.getDeviceId();

            FirmwareUpdateTask task = firmwareUpdateTaskService.find(taskId);
            log.setTaskName(task.getTaskName());
            log.setVersion(task.getVersion());

            Device device = deviceService.find(deviceId);
            log.setDeviceName(device.getDeviceName());

            list.set(i, log);
        }
        ipage.setRecords(list);
        return ipage;
    }

    /**
     * 添加固件升级计划
     * @param record 固件升级计划
     * @throws Exception 异常信息
     */
    public void insertOrUpdate(FirmwareUpdateLog record) throws Exception {
        int taskId = record.getTaskId();
        String deviceId = record.getDeviceId();

        QueryWrapper<FirmwareUpdateLog> example = new QueryWrapper<>();
        example.eq("task_id", taskId);
        example.eq("device_id", deviceId);
        example.lt("status", 3);

        Date updateTime = new Date();
        try {
            if (firmwareUpdateLogMapper.exists(example)) {
                FirmwareUpdateLog log = firmwareUpdateLogMapper.selectOne(example);
                log.setStatus(record.getStatus());
                log.setProgress(record.getProgress());
                log.setUpdateTime(updateTime);
                firmwareUpdateLogMapper.updateById(log);
            } else {
                FirmwareUpdateTask task = firmwareUpdateTaskService.find(taskId);
                int firmwareId = task.getFirmwareId();

                record.setLogId(null);
                record.setFirmwareId(firmwareId);
                record.setUpdateTime(updateTime);
                int count = firmwareUpdateLogMapper.insert(record);
                if (count < 1) {
                    throw new Exception("提交失败");
                }
            }
        } catch (Exception e) {
            throw new Exception("提交失败");
        }
    }
}
