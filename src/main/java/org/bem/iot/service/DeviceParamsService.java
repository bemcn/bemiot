package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.DeviceParamsMapper;
import org.bem.iot.model.device.DeviceParams;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备驱动参数信息
 * @author jakybland
 */
@Service
public class DeviceParamsService {
    @Resource
    DeviceParamsMapper deviceParamsMapper;

    /**
     * 查询驱动参数列表
     * @param deviceId 设备ID
     * @param groupType 分组标识 1：设备信息 2：物模型
     * @return 产品参数分组列表
     */
    public List<DeviceParams> select(String deviceId, int groupType) {
        QueryWrapper<DeviceParams> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        if(groupType > 0) {
            example.eq("group_type", groupType);
        }
        example.orderByAsc("order_num");
        return deviceParamsMapper.selectList(example);
    }
}
