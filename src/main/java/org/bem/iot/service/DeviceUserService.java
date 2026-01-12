package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.DeviceUserMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceUser;
import org.bem.iot.model.user.UserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备用户权限
 * @author jakybland
 */
@Service
public class DeviceUserService {
    @Resource
    DeviceUserMapper deviceUserMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    /**
     * 统计设备信息数量
     * @param example 查询条件
     * @return 运用授权信息数量
     */
    public long count(QueryWrapper<DeviceUser> example) {
        return deviceUserMapper.selectCount(example);
    }

    /**
     * 分页查询设备列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 设备列表
     */
    public IPage<DeviceUser> selectPage(QueryWrapper<DeviceUser> example, long index, long size) {
        Page<DeviceUser> page = new Page<>(index, size);
        IPage<DeviceUser> result = deviceUserMapper.selectPage(page, example);
        List<DeviceUser> list = result.getRecords();
        for (DeviceUser record : list) {
            String deviceId = record.getDeviceId();
            Integer userId = record.getUserId();

            if(StrUtil.isNotEmpty(deviceId)) {
                Device device = deviceMapper.selectById(deviceId);
                record.setDevice(device);
            }

            if(userId != null && userId > 0) {
                UserInfo user = userInfoMapper.selectById(userId);
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userName", user.getUserName());
                userMap.put("nickName", user.getNickName());
                userMap.put("headImg", user.getHeadImg());
                record.setUser(userMap);
            }
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 判断设备未分配用户权限
     * @param deviceUserId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotDeviceUserId(long deviceUserId) {
        QueryWrapper<DeviceUser> example = new QueryWrapper<>();
        example.eq("device_user_id", deviceUserId);
        return !deviceUserMapper.exists(example);
    }

    /**
     * 判断设备未分配用户权限
     * @param deviceId 设备ID
     * @param userId 用户ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsDeviceUser(String deviceId, int userId) {
        QueryWrapper<DeviceUser> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        example.eq("user_id", userId);
        return deviceUserMapper.exists(example);
    }

    /**
     * 查询设备
     * @param deviceUserId ID
     * @return 设备信息
     */
    public DeviceUser find(@Param("deviceUserId") long deviceUserId) {
        DeviceUser record = deviceUserMapper.selectById(deviceUserId);
        String deviceId = record.getDeviceId();
        Integer userId = record.getUserId();

        if(StrUtil.isNotEmpty(deviceId)) {
            Device device = deviceMapper.selectById(deviceId);
            record.setDevice(device);
        }

        if(userId != null && userId > 0) {
            UserInfo user = userInfoMapper.selectById(userId);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("headImg", user.getHeadImg());
            record.setUser(userMap);
        }
        return record;
    }

    /**
     * 查询设备
     * @param deviceUserId ID
     * @return 设备信息
     */
    public DeviceUser findMeta(@Param("deviceUserId") long deviceUserId) {
        return deviceUserMapper.selectById(deviceUserId);
    }

    /**
     * 添加设备
     * @param record 设备信息
     */
    public void insert(DeviceUser record) {
        record.setDeviceUserId(null);
        deviceUserMapper.insert(record);
    }

    /**
     * 修改设备
     * @param record 设备信息
     */
    public void update(@Param("record") DeviceUser record) {
        deviceUserMapper.updateById(record);
    }

    /**
     * 删除设备
     * @param deviceUserId ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("deviceUserId") long deviceUserId) throws Exception {
        return deviceUserMapper.deleteById(deviceUserId);
    }

    /**
     * 删除设备
     * @param idList ID集合
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<Long> idList) throws Exception {
        return deviceUserMapper.deleteBatchIds(idList);
    }
}
