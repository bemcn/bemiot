package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.MonitorsTaskService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 任务统计
 */
@RestController
@RequestMapping("/monitorsTask")
public class MonitorsTaskController {
    @Resource
    MonitorsTaskService monitorsTaskService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取任务统计
     */
    @GetMapping("/getTaskStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getTaskStatistics(@RequestParam(name="group", defaultValue="day") String group) {
        if(StrUtil.isEmpty(group)) {
            group = "day";
        }
        JSONObject obj;
        JSONObject jsonObject;
        switch (group) {
            case "day" -> {
                obj = monitorsTaskService.statisticsTaskDay();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "week" -> {
                obj = monitorsTaskService.statisticsTaskWeek();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "month" -> {
                obj = monitorsTaskService.statisticsTaskMonth();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            default -> jsonObject = ResponseUtil.getErrorJson("无效的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取场景统计
     */
    @GetMapping("/getSceneStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneStatistics(@RequestParam(name="group", defaultValue="day") String group) {
        if(StrUtil.isEmpty(group)) {
            group = "day";
        }
        JSONObject obj;
        JSONObject jsonObject;
        switch (group) {
            case "day" -> {
                obj = monitorsTaskService.statisticsSceneDay();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "week" -> {
                obj = monitorsTaskService.statisticsSceneWeek();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            case "month" -> {
                obj = monitorsTaskService.statisticsSceneMonth();
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
            default -> jsonObject = ResponseUtil.getErrorJson("无效的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取任务分类统计
     */
    @GetMapping("/getTaskClassStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getTaskClassStatistics() {
        JSONArray arrayObj = monitorsTaskService.statisticsTaskClass();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(arrayObj);
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取场景分组统计
     */
    @GetMapping("/getSceneGroupStatistics")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneGroupStatistics() {
        JSONArray arrayObj = monitorsTaskService.statisticsSceneGroup();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(arrayObj);
        ResponseUtil.responseData(jsonObject, response);
    }
}
