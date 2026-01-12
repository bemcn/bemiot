package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceGroupMapper;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceGroup;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备分组
 * @author jakybland
 */
@Service
public class DeviceGroupService {
    @Resource
    DeviceGroupMapper deviceGroupMapper;

    @Resource
    DeviceMapper deviceMapper;

    /**
     * 统计分组数量
     * @param example 统计条件
     * @return 分组数量
     */
    public long count(QueryWrapper<DeviceGroup> example) {
        return deviceGroupMapper.selectCount(example);
    }

    /**
     * 查询分组列表
     * @return 分组列表
     */
    public List<DeviceGroup> select() {
        QueryWrapper<DeviceGroup> example = new QueryWrapper<>();
        example.orderByAsc("order_num");
        return deviceGroupMapper.selectList(example);
    }

    /**
     * 分页查询分组列表
     * @param index 页码
     * @param size 每页数量
     * @return 分组列表
     */
    public IPage<DeviceGroup> selectPage(long index, long size) {
        QueryWrapper<DeviceGroup> example = new QueryWrapper<>();
        example.orderByAsc("order_num");
        Page<DeviceGroup> page = new Page<>(index, size);
        return deviceGroupMapper.selectPage(page, example);
    }

    /**
     * 判断分组ID是否存在
     * @param groupId 分组ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotDeviceGroupId(int groupId) {
        QueryWrapper<DeviceGroup> example = new QueryWrapper<>();
        example.eq("group_id", groupId);
        return !deviceGroupMapper.exists(example);
    }

    /**
     * 查询分组
     * @param groupId 分组ID
     * @return 分组信息
     */
    public DeviceGroup find(@Param("groupId") int groupId) {
        return deviceGroupMapper.selectById(groupId);
    }

    /**
     * 添加分组
     * @param record 分组信息
     * @throws Exception 异常信息
     */
    public void insert(DeviceGroup record) throws Exception {
        int orderNum = deviceGroupMapper.selectMax() + 1;

        record.setGroupId(null);
        record.setOrderNum(orderNum);
        int count = deviceGroupMapper.insert(record);
        if(count < 1) {
            throw new Exception("新增分组失败");
        }
    }

    /**
     * 修改分组
     * @param record 分组信息
     */
    public DeviceGroup update(@Param("record") DeviceGroup record) {
        deviceGroupMapper.updateById(record);
        return record;
    }

    /**
     * 修改排序
     * @param groupId 分组ID
     * @param orderNumber 排序值
     */
    public DeviceGroup updateOrder(@Param("groupId") int groupId, int orderNumber) {
        DeviceGroup record = deviceGroupMapper.selectById(groupId);
        record.setOrderNum(orderNumber);
        deviceGroupMapper.updateById(record);
        return record;
    }

    /**
     * 删除分组 (删除前需验证是否关联产品)
     * @param groupId 分组ID
     * @return 删除数量
     */
    public int del(@Param("groupId") int groupId) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.eq("group_id", groupId);
        List<Device> list = deviceMapper.selectList(example);
        if(!list.isEmpty()) {
            for (Device device : list) {
                device.setGroupId(0);
                deviceMapper.updateById(device);
            }
        }
        return deviceGroupMapper.deleteById(groupId);
    }

    /**
     * 批量删除分组 (删除前需验证关联产品)
     * @param idList 分组ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        QueryWrapper<Device> example = new QueryWrapper<>();
        example.in("group_id", idList);
        List<Device> list = deviceMapper.selectList(example);
        if(!list.isEmpty()) {
            for (Device device : list) {
                device.setGroupId(0);
                deviceMapper.updateById(device);
            }
        }
        return deviceGroupMapper.deleteBatchIds(idList);
    }
}
