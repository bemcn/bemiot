package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.device.DeviceChannel;
import org.bem.iot.service.DeviceChannelService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设备视频通道媒体通道
 * @author jakybland
 */
@RestController
@RequestMapping("/device_channel")
public class DeviceChannelController {
    @Resource
    DeviceChannelService deviceChannelService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备视频通道列表
     * @param deviceId 设备视频通道ID
     * @param channelType 通道类型
     * @param key 关键字
     */
    @GetMapping("/getDeviceChannelList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceChannelList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                     @RequestParam(name="channelType", defaultValue="0") Integer channelType,
                                     @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DeviceChannel> example = createExample(deviceId, channelType, key);
            List<DeviceChannel> list = deviceChannelService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备视频通道分页列表
     * @param deviceId 设备视频通道ID
     * @param channelType 通道类型
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceChannelPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceChannelPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                         @RequestParam(name="channelType", defaultValue="0") Integer channelType,
                                         @RequestParam(name="key", defaultValue="") String key,
                                         @RequestParam(name="index", defaultValue = "1") Integer index,
                                         @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DeviceChannel> example = createExample(deviceId, channelType, key);
            IPage<DeviceChannel> page = deviceChannelService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<DeviceChannel> createExample(String deviceId, Integer channelType, String key) {
        QueryWrapper<DeviceChannel> example = new QueryWrapper<>();
        if(StrUtil.isNotEmpty(deviceId)) {
            example.eq("device_id", deviceId);
        }
        if(channelType > 0) {
            example.eq("channel_type", channelType);
        }
        if(!StrUtil.isEmpty(key)) {
            example.like("channel_name", key);
        }
        example.orderByAsc("channel_id");
        return example;
    }

    /**
     * 获取设备视频通道
     * @param id id
     */
    @GetMapping("/getDeviceChannel")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceChannel(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(deviceChannelService.existsNotChannelId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备视频通道信息不存在");
        } else {
            DeviceChannel data = deviceChannelService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增设备视频通道
     * @param deviceId 设备ID
     * @param channelName 通道名称
     * @param channelType 通道类型
     * @param channel 通道序号
     * @param smtpUrl 邮件地址
     */
    @PostMapping("/addDeviceChannel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDeviceChannel(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                 @RequestParam(name="channelName", defaultValue="") String channelName,
                                 @RequestParam(name="channelType", defaultValue="0") Integer channelType,
                                 @RequestParam(name="channel", defaultValue="0") Integer channel,
                                 @RequestParam(name="smtpUrl", defaultValue="") String smtpUrl) {
        JSONObject jsonObject;
        try {
            deviceChannelService.insert(deviceId, channelName, channelType, channel, smtpUrl);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "视频通道", "新增", "新增设备视频通道，【名称】" + channelName);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑设备视频通道
     * @param channelId 视频通道ID
     * @param channelName 通道名称
     * @param channel 通道序号
     * @param smtpUrl 邮件地址
     */
    @PostMapping("/editDeviceChannel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDeviceChannel(@RequestParam(name="channelId", defaultValue="") String channelId,
                                  @RequestParam(name="channelName", defaultValue="") String channelName,
                                  @RequestParam(name="channel", defaultValue="0") Integer channel,
                                  @RequestParam(name="smtpUrl", defaultValue="") String smtpUrl) {
        JSONObject jsonObject;
        try {
            deviceChannelService.update(channelId, channelName, channel, smtpUrl);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "视频通道", "修改", "修改设备视频通道，【名称】" + channelName);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改运行状态
     * @param id id
     * @param types 类型 0：推流状态 1：录像状态 2：录像存储状态
     * @param status 状态 0：停止 1：运行中
     */
    @GetMapping("/runStatusDeviceChannel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void runStatusDeviceChannel(@RequestParam(name="id", defaultValue="") String id,
                                     @RequestParam(name="types", defaultValue="0") Integer types,
                                     @RequestParam(name="status", defaultValue="0") Integer status) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(id)) {
                jsonObject = ResponseUtil.getErrorJson("id不能为空");
            } else if(deviceChannelService.existsNotChannelId(id)) {
                jsonObject = ResponseUtil.getErrorJson("设备视频通道信息不存在");
            } else if(status < 0 || status > 1) {
                jsonObject = ResponseUtil.getErrorJson("状态标识错误");
            } else {
                deviceChannelService.updateRunStatus(id, types, status);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除设备视频通道
     * @param id id
     */
    @GetMapping("/delDeviceChannel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceChannel(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!deviceChannelService.existsNotChannelId(id)) {
                DeviceChannel device = deviceChannelService.find(id);
                String channelName = device.getChannelName();
                int count = deviceChannelService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "视频通道", "删除", "删除设备视频通道，【名称】" + channelName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个设备视频通道
     * @param ids id集合
     */
    @GetMapping("/delDeviceChannels")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceChannels(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = deviceChannelService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "视频通道", "删除", "批量删除" + count + "条设备视频通道信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
