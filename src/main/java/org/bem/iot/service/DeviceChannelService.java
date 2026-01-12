package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceChannel;
import org.bem.iot.model.device.DeviceMonitoring;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.video.VideoServer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 设备视频通道
 * @author jakybland
 */
@Service
public class DeviceChannelService {
    @Resource
    DeviceChannelMapper deviceChannelMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    VideoServerMapper videoServerMapper;

    @Resource
    DeviceMonitoringMapper deviceMonitoringMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 统计视频通道数量
     * @param example 统计条件
     * @return 视频通道数量
     */
    public long count(QueryWrapper<DeviceChannel> example) {
        return deviceChannelMapper.selectCount(example);
    }

    /**
     * 查询视频通道列表
     * @param example 查询条件
     * @return 视频通道列表
     */
    public List<DeviceChannel> select(QueryWrapper<DeviceChannel> example) {
        List<DeviceChannel> list = deviceChannelMapper.selectList(example);
        for (DeviceChannel channel : list) {
            String channelId = channel.getChannelId();
            String productId = channel.getProductId();
            String deviceId = channel.getDeviceId();

            Product product = productMapper.selectById(productId);
            Device device = deviceMapper.selectById(deviceId);
            DeviceMonitoring monitoring = queryDeviceMonitoring(deviceId);
            String serverId = monitoring.getServerId();
            VideoServer server = videoServerMapper.selectById(serverId);

            boolean[] status = queryChannelStatus(channelId);

            channel.setPushStatus(status[0]);
            channel.setRecordStatus(status[1]);
            channel.setProduct(product);
            channel.setDevice(device);
            channel.setMonitoring(monitoring);
            channel.setServer(server);
        }
        return list;
    }
    private DeviceMonitoring queryDeviceMonitoring(String deviceId) {
        QueryWrapper<DeviceMonitoring> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        return deviceMonitoringMapper.selectOne(example);
    }
    private boolean[] queryChannelStatus(String channelId) {
        String key1 = "device_channel::push_status::" + channelId;
        String key2 = "device_channel::record_status::" + channelId;
        boolean[] status = {false, false};
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key1))) {
            status[0] = Boolean.parseBoolean(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key1)));
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key2))) {
            status[1] = Boolean.parseBoolean(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key2)));
        }
        return status;
    }

    /**
     * 分页查询视频通道列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 视频通道列表
     */
    public IPage<DeviceChannel> selectPage(QueryWrapper<DeviceChannel> example, long index, long size) {
        Page<DeviceChannel> page = new Page<>(index, size);
        IPage<DeviceChannel> pageData = deviceChannelMapper.selectPage(page, example);
        List<DeviceChannel> list = pageData.getRecords();
        for (DeviceChannel channel : list) {
            String channelId = channel.getChannelId();
            String productId = channel.getProductId();
            String deviceId = channel.getDeviceId();

            Product product = productMapper.selectById(productId);
            Device device = deviceMapper.selectById(deviceId);
            DeviceMonitoring monitoring = queryDeviceMonitoring(deviceId);
            String serverId = monitoring.getServerId();
            VideoServer server = videoServerMapper.selectById(serverId);

            boolean[] status = queryChannelStatus(channelId);

            channel.setPushStatus(status[0]);
            channel.setRecordStatus(status[1]);
            channel.setProduct(product);
            channel.setDevice(device);
            channel.setMonitoring(monitoring);
            channel.setServer(server);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断视频通道ID是否存在
     * @param channelId 视频通道ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotChannelId(String channelId) {
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        example.eq("channel_id", channelId);
        return !deviceChannelMapper.exists(example);
    }

    /**
     * 查询视频通道
     * @param channelId 视频通道ID
     * @return 视频通道信息
     */
    public DeviceChannel find(@Param("channelId") String channelId) {
        DeviceChannel channel = deviceChannelMapper.selectById(channelId);
        String productId = channel.getProductId();
        String deviceId = channel.getDeviceId();

        Product product = productMapper.selectById(productId);
        Device device = deviceMapper.selectById(deviceId);
        DeviceMonitoring monitoring = queryDeviceMonitoring(deviceId);
        String serverId = monitoring.getServerId();
        VideoServer server = videoServerMapper.selectById(serverId);

        boolean[] status = queryChannelStatus(channelId);

        channel.setPushStatus(status[0]);
        channel.setRecordStatus(status[1]);
        channel.setProduct(product);
        channel.setDevice(device);
        channel.setMonitoring(monitoring);
        channel.setServer(server);
        return channel;
    }

    /**
     * 添加视频通道
     * @param deviceId 设备ID
     * @param channelName 通道名称
     * @param channelType 通道类型
     * @param channel 通道序号
     * @param smtpUrl 邮件地址
     */
    public void insert(String deviceId, String channelName, int channelType, int channel, String smtpUrl) {
        Device device = deviceMapper.selectById(deviceId);
        String productId = device.getProductId();
        String devChannelId = device.getChannelId();

        //获取同类设备最大序号
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        example.likeRight("channel_id", devChannelId);
        int lastChannel = deviceChannelMapper.selectMax(example) + 1;
        String channelNum;
        if(lastChannel < 10) {
            channelNum = "00" + lastChannel;
        } else if(lastChannel < 100) {
            channelNum = "0" + lastChannel;
        } else {
            channelNum = lastChannel + "";
        }
        String channelId = devChannelId + channelNum;
        Date createDate = new Date();

        DeviceChannel record = new DeviceChannel();
        record.setChannelId(channelId);
        record.setDeviceId(deviceId);
        record.setProductId(productId);
        record.setChannelName(channelName);
        record.setChannelType(channelType);
        record.setChannelNumber(lastChannel);
        record.setChannel(channel);
        record.setSmtpUrl(smtpUrl);
        record.setCreateTime(createDate);
        deviceChannelMapper.insert(record);
    }

    /**
     * 修改视频通道
     * @param channelId 视频通道ID
     * @param channelName 通道名称
     * @param channel 通道序号
     * @param smtpUrl 邮件地址
     */
    public void update(String channelId, String channelName, int channel, String smtpUrl) {
        DeviceChannel record = deviceChannelMapper.selectById(channelId);
        record.setChannelName(channelName);
        record.setChannel(channel);
        record.setSmtpUrl(smtpUrl);
        deviceChannelMapper.updateById(record);
    }

    /**
     * 修改通道运行状态
     * @param channelId 视频通道ID
     * @param types 类型 0：推流状态 1：录像状态
     * @param status 状态 0：停止 1：运行中
     */
    public void updateRunStatus(@Param("channelId") String channelId, int types, int status) {
        String key1 = "device_channel::push_status::" + channelId;
        String key2 = "device_channel::record_status::" + channelId;
        if(status == 0) {
            stringRedisTemplate.opsForValue().set(key1, "false");
            stringRedisTemplate.opsForValue().set(key2, "false");
        } else {
            if(types == 0) {
                stringRedisTemplate.opsForValue().set(key1, "true");
                stringRedisTemplate.opsForValue().set(key2, "false");
            } else {
                stringRedisTemplate.opsForValue().set(key1, "false");
                stringRedisTemplate.opsForValue().set(key2, "true");
            }
        }
    }

    /**
     * 删除视频通道 (删除前需验证是否存在设备)
     * @param channelId 视频通道ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("channelId") String channelId) throws Exception {
        return deviceChannelMapper.deleteById(channelId);
    }

    /**
     * 批量删除视频通道 (删除前需验证是否存在设备)
     * @param idList 视频通道ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<String> idList) throws Exception {
        return deviceChannelMapper.deleteBatchIds(idList);
    }
}
