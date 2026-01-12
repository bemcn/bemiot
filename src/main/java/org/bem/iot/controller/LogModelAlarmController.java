package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.UserExp;
import org.bem.iot.model.log.LogModelAlarm;
import org.bem.iot.service.LogModelAlarmService;
import org.bem.iot.util.ExcelUtil;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 物模型操作日志
 * @author jakybland
 */
@RestController
@RequestMapping("/logModelAlarm")
public class LogModelAlarmController {
    @Resource
    LogModelAlarmService logModelAlarmService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取指定数量最新日志列表
     * @param deviceId 设备ID
     * @param identity 物模型标识
     * @param status 状态 1：告警中 2：已解除
     * @param level 告警等级 1：一般告警 2：重要告警 3：紧急告警
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param size 显示数量
     */
    @GetMapping("/getAlarmLogTop")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getAlarmLogTop(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                    @RequestParam(name="identity", defaultValue="") String identity,
                                    @RequestParam(name="status", defaultValue="0") Integer status,
                                    @RequestParam(name="level", defaultValue="0") Integer level,
                                    @RequestParam(name="startDate", defaultValue="") String startDate,
                                    @RequestParam(name="endDate", defaultValue="") String endDate,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(deviceId)) {
                example.eq("device_id", deviceId);
            }
            if (StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if (status > 0) {
                example.eq("alarm_status", status);
            }
            if (level > 0) {
                example.eq("level", level);
            }
            if (!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
                Date starTime;
                Date endTime;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    starTime = format.parse(startDate);
                    endTime = format.parse(endDate);
                    example.between("ts", starTime.getTime(), endTime.getTime());
                } catch (ParseException ignored) {
                }
            }
            example.orderByDesc("ts");
            List<LogModelAlarm> list = logModelAlarmService.selectLimit(example, size);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取日志分页列表
     * @param deviceId 设备ID
     * @param identity 物模型标识
     * @param status 状态 1：告警中 2：已解除
     * @param level 告警等级 1：一般告警 2：重要告警 3：紧急告警
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getAlarmLogPageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getAlarmLogPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                    @RequestParam(name="identity", defaultValue="") String identity,
                                    @RequestParam(name="status", defaultValue="0") Integer status,
                                    @RequestParam(name="level", defaultValue="0") Integer level,
                                    @RequestParam(name="startDate", defaultValue="") String startDate,
                                    @RequestParam(name="endDate", defaultValue="") String endDate,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(deviceId)) {
                example.eq("device_id", deviceId);
            }
            if (StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if (status > 0) {
                example.eq("alarm_status", status);
            }
            if (level > 0) {
                example.eq("level", level);
            }
            if (!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
                Date starTime;
                Date endTime;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    starTime = format.parse(startDate);
                    endTime = format.parse(endDate);
                    example.between("ts", starTime.getTime(), endTime.getTime());
                } catch (ParseException ignored) {
                }
            }
            example.orderByDesc("ts");
            IPage<LogModelAlarm> page = logModelAlarmService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取告警合计
     */
    @GetMapping("/totalAlarmLogLevel")
    @PublicHeadLimit("user")
    @ResponseBody
    public void totalAlarmLogLevel() {
        JSONObject jsonObject;
        try {
            JSONObject obj = logModelAlarmService.countByLevel();
            jsonObject = ResponseUtil.getSuccessJson(obj);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取近7天告警合计
     */
    @GetMapping("/totalAlarmLogSevenDay")
    @PublicHeadLimit("user")
    @ResponseBody
    public void totalAlarmLogSevenDay() {
        JSONObject jsonObject;
        try {
            List<Map<String, Object>> list = logModelAlarmService.countBySevenDays();
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备告警排行
     */
    @GetMapping("/rankingAlarmLogDevice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void rankingAlarmLogDevice(@RequestParam(name="size", defaultValue="10") Integer size) {
        JSONObject jsonObject;
        try {
            List<Map<String, Object>> list = logModelAlarmService.countByDeviceRanking(size);
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除日志
     * @param id id
     */
    @GetMapping("/delAlarmLog")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delAlarmLog(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            logModelAlarmService.del(id);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个日志
     * @param ids id集合
     */
    @GetMapping("/delAlarmLogs")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delAlarmLogs(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                logModelAlarmService.delArray(idList);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导出Excel
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                            @RequestParam(name="identity", defaultValue="") String identity,
                            @RequestParam(name="status", defaultValue="0") Integer status,
                            @RequestParam(name="level", defaultValue="0") Integer level,
                            @RequestParam(name="startDate", defaultValue="") String startDate,
                            @RequestParam(name="endDate", defaultValue="") String endDate) throws IOException {
        QueryWrapper<LogModelAlarm> example = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(deviceId)) {
            example.eq("device_id", deviceId);
        }
        if (StrUtil.isNotEmpty(identity)) {
            example.eq("model_identity", identity);
        }
        if (status > 0) {
            example.eq("alarm_status", status);
        }
        if (level > 0) {
            example.eq("level", level);
        }
        if (!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
            Date starTime;
            Date endTime;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                starTime = format.parse(startDate);
                endTime = format.parse(endDate);
                example.between("ts", starTime.getTime(), endTime.getTime());
            } catch (ParseException ignored) {
            }
        }
        example.orderByDesc("ts");
        List<LogModelAlarm> list = logModelAlarmService.selectLimit(example, 1000);

        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("告警日志") + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream(), UserExp.class)
                .useDefaultStyle(false)
                .sheet("Sheet1")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(list);
    }
}
