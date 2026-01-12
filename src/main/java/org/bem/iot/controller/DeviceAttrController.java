package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceAttr;
import org.bem.iot.service.DeviceAttrService;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备档案属性
 * @author jakybland
 */
@RestController
@RequestMapping("/deviceAttr")
public class DeviceAttrController {
    @Resource
    DeviceAttrService deviceAttrService;

    @Resource
    DeviceService deviceService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备档案属性列表
     * @param deviceId 设备ID
     */
    @GetMapping("/getDeviceAttrList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceAttrList(@RequestParam(name="deviceId", defaultValue="") String deviceId) {
        JSONObject jsonObject;
        try {
            List<DeviceAttr> list = deviceAttrService.select(deviceId);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备档案属性分页列表
     * @param deviceId 设备ID
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceAttrPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceAttrPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                      @RequestParam(name="index", defaultValue = "1") Integer index,
                                      @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DeviceAttr> example = new QueryWrapper<>();
            example.eq("device_id", deviceId);
            example.orderByAsc("attr_id");
            IPage<DeviceAttr> page = deviceAttrService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 批量添加设备档案属性
     * @param deviceId 设备ID
     * @param attrs 属性值集合
     */
    @PostMapping("/addDeviceAttrArray")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDeviceAttrArray(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                   @RequestParam(name="attrs", defaultValue="") String attrs) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(deviceId)) {
                jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
            } else if(StrUtil.isEmpty(attrs)) {
                jsonObject = ResponseUtil.getErrorJson("属性值不能为空");
            } else {
                JSONArray recordArray = JSONArray.parseArray(attrs);
                if(recordArray.isEmpty()) {
                    jsonObject = ResponseUtil.getErrorJson("属性值不能为空");
                } else {
                    Device dev = deviceService.findMeta(deviceId);
                    String deviceName = dev.getDeviceName();
                    deviceAttrService.insertArray(deviceId, recordArray);

                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "资产档案", "编辑", "编辑设备资产档案，【设备】" + deviceName);

                    jsonObject = ResponseUtil.getSuccessJson();
                }
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}