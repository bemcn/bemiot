package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.MonitorsMessageService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 消息监控 SSE
 */
@RestController
@RequestMapping("/monitorsMessage")
public class MonitorsMessageController {
    @Resource
    MonitorsMessageService monitorsMessageService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取消息分类统计
     */
    @GetMapping("/getClassStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getClassStatistics(@RequestParam(name="group", defaultValue="day") String group) {
        if(StrUtil.isEmpty(group)) {
            group = "day";
        }
        JSONObject obj;
        JSONObject jsonObject;
        switch (group) {
            case "day" -> {
                obj = monitorsMessageService.statisticsClassDay();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "week" -> {
                obj = monitorsMessageService.statisticsClassWeek();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "month" -> {
                obj = monitorsMessageService.statisticsClassMonth();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            default -> jsonObject = ResponseUtil.getErrorJson("无效的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取消息分类总计
     */
    @GetMapping("/getClassTotal")
    @PublicHeadLimit
    @ResponseBody
    public void getClassTotal() {
        JSONArray objArray = monitorsMessageService.statisticsClassTotal();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(objArray);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取连接统计
     */
    @GetMapping("/getConnectStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getConnectStatistics(@RequestParam(name="group", defaultValue="day") String group) {
        if(StrUtil.isEmpty(group)) {
            group = "day";
        }
        JSONObject obj;
        JSONObject jsonObject;
        switch (group) {
            case "day" -> {
                obj = monitorsMessageService.statisticsConnectDay();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "week" -> {
                obj = monitorsMessageService.statisticsConnectWeek();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "month" -> {
                obj = monitorsMessageService.statisticsConnectMonth();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            default -> jsonObject = ResponseUtil.getErrorJson("无效的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取队列统计
     */
    @GetMapping("/getQueueStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getQueueStatistics(@RequestParam(name="group", defaultValue="day") String group) {
        if(StrUtil.isEmpty(group)) {
            group = "day";
        }
        JSONObject obj;
        JSONObject jsonObject;
        switch (group) {
            case "day" -> {
                obj = monitorsMessageService.statisticsQueueDay();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "week" -> {
                obj = monitorsMessageService.statisticsQueueWeek();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "month" -> {
                obj = monitorsMessageService.statisticsQueueMonth();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            default -> jsonObject = ResponseUtil.getErrorJson("无效的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
