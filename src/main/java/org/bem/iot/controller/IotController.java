package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.IotService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * IOT
 * @author jakybland
 */
@RestController
@RequestMapping("/iot")
public class IotController {

    @Resource
    IotService iotService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取IOT物模型实时数据
     * @param deviceId 设备ID
     * @param identity 物模型ID
     */
    @GetMapping("/realTimeData")
    @PublicHeadLimit
    @ResponseBody
    public void realTimeData(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                             @RequestParam(name="identity", defaultValue="") String identity) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(StrUtil.isEmpty(identity)) {
            jsonObject = ResponseUtil.getErrorJson("物模型标识不能为空");
        } else {
            JSONObject obj = iotService.realTime(deviceId, identity);
            jsonObject = ResponseUtil.getSuccessJson(obj);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 查询物模型历史记录
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     * @param queryOut 查询方式
     */
    @GetMapping("/historicalOne")
    @PublicHeadLimit
    @ResponseBody
    public void historicalOne(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                              @RequestParam(name="identity", defaultValue="") String identity,
                              @RequestParam(name="timeFrame", defaultValue="") String timeFrame,
                              @RequestParam(name="timeData", defaultValue="") String timeData,
                              @RequestParam(name="queryOut", defaultValue="") String queryOut) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(StrUtil.isEmpty(identity)) {
            jsonObject = ResponseUtil.getErrorJson("物模型标识不能为空");
        } else {
            JSONObject obj = iotService.historicalOneValue(deviceId, identity, timeFrame, timeData, queryOut);
            jsonObject = ResponseUtil.getSuccessJson(obj);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 查询设备历史记录
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     */
    @GetMapping("/historicalDeviceFind")
    @PublicHeadLimit
    @ResponseBody
    public void historicalDeviceFind(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                     @RequestParam(name="identity", defaultValue="") String identity,
                                     @RequestParam(name="timeFrame", defaultValue="") String timeFrame,
                                     @RequestParam(name="timeData", defaultValue="") String timeData) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(StrUtil.isEmpty(identity)) {
            jsonObject = ResponseUtil.getErrorJson("物模型标识不能为空");
        } else {
            Map<String, Object> data = iotService.historicalFind(deviceId, identity, timeFrame, timeData);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 分页查询设设备历史记录
     * @param deviceId 设备ID
     * @param identity 物模型ID
     * @param timeFrame 日期查询方式
     * @param timeData 日期查询数据
     * @param orderBy 排序
     * @param index 当前页码
     * @param size 每页显示数量
     */
    @GetMapping("/historicalDeviceQuery")
    @PublicHeadLimit
    @ResponseBody
    public void historicalPageQuery(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                    @RequestParam(name="identity", defaultValue="") String identity,
                                    @RequestParam(name="timeFrame", defaultValue="") String timeFrame,
                                    @RequestParam(name="timeData", defaultValue="") String timeData,
                                    @RequestParam(name="orderBy", defaultValue="") String orderBy,
                                    @RequestParam(name="index", defaultValue="0") Integer index,
                                    @RequestParam(name="size", defaultValue="20") Integer size) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空");
        } else if(StrUtil.isEmpty(identity)) {
            jsonObject = ResponseUtil.getErrorJson("物模型标识不能为空");
        } else {
            IPage<Map<String, Object>> pageData = iotService.historicalPage(deviceId, identity, timeFrame, timeData, orderBy, index, size);
            jsonObject = ResponseUtil.getSuccessJson(pageData);
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
