package org.bem.iot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.statistics.MsgStatistics;
import org.bem.iot.model.statistics.MsgStatisticsMonth;
import org.bem.iot.model.log.LogSystem;
import org.bem.iot.model.system.SystemNotice;
import org.bem.iot.service.*;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

/**
 * 运用信息
 * @author jaky
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Resource
    ProductService productService;

    @Resource
    DeviceService deviceService;

    @Resource
    SystemNoticeService systemNoticeService;

    @Resource
    MsgStatisticsService msgStatisticsService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取总计
     */
    @GetMapping("/getIotTotal")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getIotTotal() {
        JSONObject jsonObject;
        try {
            JSONObject data = queryIotTotal();
            jsonObject = ResponseUtil.getSuccessJson(data);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取消息分时统计
     */
    @GetMapping("/getMessageHourStatistics")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getMessageHourStatistics() {
        JSONObject jsonObject;
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            JSONObject data = msgStatisticsService.dayStatisticsChart(year, month, day);
            jsonObject = ResponseUtil.getSuccessJson(data);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取最新公告
     */
    @GetMapping("/getNoticeTop")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getNoticeTop(@RequestParam(name="size", defaultValue = "10") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<SystemNotice> example = new QueryWrapper<>();
            example.orderByDesc("create_time");
            List<SystemNotice> list = systemNoticeService.selectTop(example, size);
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备类型比例
     */
    @GetMapping("/getDevicesTypeTotal")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getDevicesTypeTotal() {
        JSONObject jsonObject;
        try {
            JSONObject data = queryDevTypeTotal();
            jsonObject = ResponseUtil.getSuccessJson(data);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更新数据
     */
    @GetMapping("/getUpDateBase")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getUpDateBase() {
        JSONObject jsonObject;
        try {
            JSONObject totalJson = queryIotTotal();
            JSONObject hourJson = queryHourStatistics();
            JSONObject typeJson = queryDevTypeTotal();

            JSONObject data = new JSONObject();
            data.put("totalStatistics", totalJson);
            data.put("hourStatistics", hourJson);
            data.put("devTypeStatistics", typeJson);

            jsonObject = ResponseUtil.getSuccessJson(data);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 总计
     * @return 总计
     */
    private JSONObject queryIotTotal() {
        // 产品数量
        QueryWrapper<Product> productQuery = new QueryWrapper<>();
        long productCount = productService.count(productQuery);

        // 产品待发布数量
        productQuery.eq("status", 2);
        long productEnableCount = productService.count(productQuery);

        // 产品已发布数量
        productQuery = new QueryWrapper<>();
        productQuery.eq("status", 1);
        long productDeactivatedCount = productService.count(productQuery);

        // 设备数量
        QueryWrapper<Device> deviceQuery = new QueryWrapper<>();
        deviceQuery.eq("status", 3);
        long deviceCount = deviceService.count(deviceQuery);

        //设备在线统计
        long deviceOnlineCount = deviceService.onlineCount();

        //设备离线统计
        long deviceOfflineCount = deviceCount - deviceOnlineCount;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        MsgStatistics msgStatist = msgStatisticsService.findRealTime();
        MsgStatisticsMonth msgStatistMonth = msgStatisticsService.findMonth(year, month);

        //设备操作总数
        long optionCount = msgStatistMonth.getSendCount() + msgStatist.getSendCount();

        //设备操作月消息总数
        long optionMonthCount = msgStatistMonth.getSendCount();

        //设备操作日消息总数
        long optionDayCount = msgStatist.getSendCount();

        //设备消息总数
        long messageCount = msgStatistMonth.getGatherCount() + msgStatist.getGatherCount();

        //设备消息月消息总数
        long messageMonthCount = msgStatistMonth.getGatherCount();

        //设备消息日消息总数
        long messageDayCount = msgStatist.getGatherCount();

        //设备告警总数
        long alarmCount = msgStatistMonth.getAlarmCount() + msgStatist.getAlarmCount();

        //设备告警月消息总数
        long alarmMonthCount = msgStatistMonth.getAlarmCount();

        //设备告警日消息总数
        long alarmDayCount = msgStatist.getAlarmCount();

        //上报事件总数
        long eventCount = msgStatistMonth.getEventCount() + msgStatist.getEventCount();

        //上报事件月消息总数
        long eventMonthCount = msgStatistMonth.getEventCount();

        //上报事件日消息总数
        long eventDayCount = msgStatist.getEventCount();

        JSONObject data = new JSONObject();
        data.put("productCount", productCount);
        data.put("productEnableCount", productEnableCount);
        data.put("productDeactivatedCount", productDeactivatedCount);
        data.put("deviceCount", deviceCount);
        data.put("deviceOnlineCount", deviceOnlineCount);
        data.put("deviceOfflineCount", deviceOfflineCount);
        data.put("optionCount", optionCount);
        data.put("optionMonthCount", optionMonthCount);
        data.put("optionDayCount", optionDayCount);
        data.put("messageCount", messageCount);
        data.put("messageMonthCount", messageMonthCount);
        data.put("messageDayCount", messageDayCount);
        data.put("alarmCount", alarmCount);
        data.put("alarmMonthCount", alarmMonthCount);
        data.put("alarmDayCount", alarmDayCount);
        data.put("eventCount", eventCount);
        data.put("eventMonthCount", eventMonthCount);
        data.put("eventDayCount", eventDayCount);
        return data;
    }

    /**
     * 消息分时统计
     * @return 分时统计
     */
    private JSONObject queryHourStatistics() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return msgStatisticsService.dayStatisticsChart(year, month, day);
    }

    /**
     * 设备分类统计
     * @return 分类统计
     */
    private JSONObject queryDevTypeTotal() {
        // 设备数量
        QueryWrapper<Device> deviceQuery = new QueryWrapper<>();
        deviceQuery.eq("status", 3);
        List<Device> list = deviceService.select(deviceQuery);
        long connection = list.stream().filter(device -> device.getProduct().getTypes() == 1).count();
        long gateway = list.stream().filter(device -> device.getProduct().getTypes() == 2).count();
        long monitor = list.stream().filter(device -> device.getProduct().getTypes() == 3).count();
        long monitorStorage = list.stream().filter(device -> device.getProduct().getTypes() == 4).count();
        long subDevice = list.stream().filter(device -> device.getProduct().getTypes() == 5).count();
        long virtual = list.stream().filter(device -> device.getProduct().getTypes() == 6).count();

        JSONObject connectionJson = new JSONObject();
        connectionJson.put("name", "直连设备");
        connectionJson.put("value", connection);

        JSONObject gatewayJson = new JSONObject();
        gatewayJson.put("name", "网关设备");
        gatewayJson.put("value", gateway);

        JSONObject monitorJson = new JSONObject();
        monitorJson.put("name", "监测设备");
        monitorJson.put("value", monitor);

        JSONObject monitorStorageJson = new JSONObject();
        monitorStorageJson.put("name", "视频存储设备");
        monitorStorageJson.put("value", monitorStorage);

        JSONObject subDeviceJson = new JSONObject();
        subDeviceJson.put("name", "网关子设备");
        subDeviceJson.put("value", subDevice);

        JSONObject virtualJson = new JSONObject();
        virtualJson.put("name", "虚拟设备");
        virtualJson.put("value", virtual);

        JSONObject data = new JSONObject();
        data.put("connection", connectionJson);
        data.put("gateway", gatewayJson);
        data.put("monitor", monitorJson);
        data.put("monitorStorage", monitorStorageJson);
        data.put("subDevice", subDeviceJson);
        data.put("virtual", virtualJson);
        return data;
    }

    /**
     * 获取指定数量日志
     * @param size 数量
     */
    @GetMapping("/getLogTop")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getLogTop(@RequestParam(name="size", defaultValue = "10") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogSystem> example = new QueryWrapper<>();
            example.orderByDesc("ts");
            List<LogSystem> list = logSystemService.selectLimit(example, size);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
