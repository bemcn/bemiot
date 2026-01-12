package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.general.Firmware;
import org.bem.iot.model.general.FirmwareUpdateLog;
import org.bem.iot.model.general.FirmwareUpdateTask;
import org.bem.iot.model.product.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 固件升级计划
 * @author jakybland
 */
@Service
public class FirmwareUpdateTaskService {
    @Resource
    FirmwareUpdateTaskMapper firmwareUpdateTaskMapper;

    @Resource
    FirmwareMapper firmwareMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    DeviceMapper deviceMapper;

    FirmwareUpdateLogMapper firmwareUpdateLogMapper;

    /**
     * 分页查询固件升级计划列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 固件列表
     */
    public IPage<FirmwareUpdateTask> selectPage(QueryWrapper<FirmwareUpdateTask> example, long index, long size) {
        Page<FirmwareUpdateTask> page = new Page<>(index, size);
        return firmwareUpdateTaskMapper.selectPage(page, example);
    }

    /**
     * 判断任务ID是否存在
     * @param taskId 任务ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotFirmwareTaskId(int taskId) {
        QueryWrapper<FirmwareUpdateTask> example = new QueryWrapper<>();
        example.eq("firmwareUpdateTask_id", taskId);
        return !firmwareUpdateTaskMapper.exists(example);
    }

    /**
     * 查询固件升级计划
     * @param taskId 任务ID
     * @return 固件信息
     */
    @Cacheable(value = "firmwareUpdateTask", key = "#p0")
    public FirmwareUpdateTask find(@Param("taskId") int taskId) {
        return firmwareUpdateTaskMapper.selectById(taskId);
    }

    /**
     * 添加固件升级计划
     * @param record 固件升级计划
     * @throws Exception 异常信息
     */
    public void insert(FirmwareUpdateTask record) throws Exception {
        Date createTime = new Date();
        Date planTime = record.getPlanTime();
        if(planTime != null) {
            long planTimer = planTime.getTime();
            long nowTimer = createTime.getTime();
            if (planTimer <= nowTimer) {
                throw new Exception("计划时间不能小于当前时间");
            }
        }
        int firmwareId = record.getFirmwareId();
        Firmware firmware = firmwareMapper.selectById(firmwareId);
        String version = firmware.getVersion();

        QueryWrapper<Product> examplePro = new QueryWrapper<>();
        examplePro.eq("firmware_id", firmwareId);
        if(productMapper.exists(examplePro)) {
            Product product = productMapper.selectOne(examplePro);
            String productId = product.getProductId();

            QueryWrapper<Device> exampleDev = new QueryWrapper<>();
            long devCount;
            if(record.getTaskType() == 1) {
                exampleDev.eq("product_id", productId);
                devCount = deviceMapper.selectCount(exampleDev);
                record.setDevices("[]");
            } else {
                String devices = record.getDevices();
                if(devices == null || StrUtil.isEmpty(devices)) {
                    throw new Exception("未选择设备");
                } else {
                    List<Integer> idList = Arrays.stream(devices.split(",")).map(Integer::parseInt).toList();
                    exampleDev.in("device_id", idList);
                    devCount = deviceMapper.selectCount(exampleDev);
                    if(devCount > 0L) {
                        List<Device> devList = deviceMapper.selectList(exampleDev);
                        JSONArray jsonArray = new JSONArray();
                        for (Device device : devList) {
                            JSONObject obj = new JSONObject();
                            obj.put("deviceId", device.getDeviceId());
                            obj.put("deviceName", device.getDeviceName());
                            obj.put("spaceRouteName", device.getSpaceRouteName());
                            jsonArray.add(obj);
                        }
                        record.setDevices(jsonArray.toJSONString());
                    } else {
                        throw new Exception("未选择设备");
                    }
                }
            }

            record.setTaskId(null);
            record.setVersion(version);
            record.setDeviceCount((int) devCount);
            record.setCreateTime(createTime);
            int count = firmwareUpdateTaskMapper.insert(record);
            if (count < 1) {
                throw new Exception("计划发布失败");
            }
        } else {
            throw new Exception("该固件尚未绑定产品，计划发布无效");
        }
    }

    /**
     * 删除固件 (删除前需验证是否关联产品)
     * @param taskId 任务ID
     * @return 删除数量
     */
    @CacheEvict(value = "firmwareUpdateTask", key = "#p0")
    public int del(@Param("taskId") int taskId) {
        if(hasTaskLog(taskId)) {
            throw new RuntimeException("已有设备执行升级，禁止删除");
        } else {
            return firmwareUpdateTaskMapper.deleteById(taskId);
        }
    }

    private boolean hasTaskLog(int taskId) {
        QueryWrapper<FirmwareUpdateLog> example = new QueryWrapper<>();
        example.in("task_id", taskId);
        return firmwareUpdateLogMapper.exists(example);
    }
}
