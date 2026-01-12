package org.bem.iot.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.SystemConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地图控制器
 * @author jakybland
 */
@RestController
@RequestMapping("/map")
public class MapController {
    @Resource
    SystemConfigService systemConfigService;

    @Resource
    DeviceService deviceService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备地图信息
     */
    @GetMapping("/getDeviceMap")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceMap() {
        //获取地图初始中心点  mapCenter
        //获取设备地图范围 mapRange
        //获取设备分布
    }
}
