package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.log.LogModelEvent;
import org.bem.iot.service.LogModelEventService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 物模型事件日志
 * @author jakybland
 */
@RestController
@RequestMapping("/log_model_event")
public class LogModelEventController {
    @Resource
    LogModelEventService logModelEventService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取指定数量最新日志列表
     * @param deviceId 设备ID
     * @param identity 物模型标识
     * @param type 事件类型<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; register：设备注册<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; online: 设备上线<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; offline：设备下线<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; reported: 事件上报<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; read：读属性<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; readReply：读属性反馈<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; write：写属性<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; writeReply：写属性反馈
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param size 显示数量
     */
    @GetMapping("/getEventLogTop")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getEventLogTop(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                @RequestParam(name="identity", defaultValue="") String identity,
                                @RequestParam(name="type", defaultValue="") String type,
                                @RequestParam(name="startDate", defaultValue="") String startDate,
                                @RequestParam(name="endDate", defaultValue="") String endDate,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogModelEvent> example = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(deviceId)) {
                example.eq("device_id", deviceId);
            }
            if (StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if (StrUtil.isNotEmpty(type)) {
                example.eq("type", type);
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
            List<LogModelEvent> list = logModelEventService.selectLimit(example, size);

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
     * @param type 事件类型<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; register：设备注册<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; online: 设备上线<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; offline：设备下线<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; reported: 事件上报<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; read：读属性<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; readReply：读属性反馈<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; write：写属性<br/>
     *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; writeReply：写属性反馈
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getEventLogPageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getEventLogPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                    @RequestParam(name="identity", defaultValue="") String identity,
                                    @RequestParam(name="type", defaultValue="") String type,
                                    @RequestParam(name="startDate", defaultValue="") String startDate,
                                    @RequestParam(name="endDate", defaultValue="") String endDate,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<LogModelEvent> example = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(deviceId)) {
                example.eq("device_id", deviceId);
            }
            if (StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if (StrUtil.isNotEmpty(type)) {
                example.eq("type", type);
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
            IPage<LogModelEvent> page = logModelEventService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除日志
     * @param id id
     */
    @GetMapping("/delEventLog")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delLog(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            logModelEventService.del(id);
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
    @GetMapping("/delEventLogs")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delLogs(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                logModelEventService.delArray(idList);
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
