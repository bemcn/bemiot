package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.ScheduleStatus;
import org.bem.iot.model.general.Schedule;
import org.bem.iot.service.ScheduleService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 定时任务
 * @author jakybland
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    @Resource
    ScheduleService scheduleService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取定时任务信息列表
     * @param scheduleGroup 定时任务组 device：设备物模型  camera：视频监控  database:数据库  monitor：监控中心
     * @param key 关键字
     */
    @GetMapping("/getScheduleList")
    @PublicHeadLimit
    @ResponseBody
    public void getScheduleList(@RequestParam(name="scheduleGroup", defaultValue="") String scheduleGroup,
                                @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Schedule> example = createExample(scheduleGroup, key);
            List<Schedule> list = scheduleService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取定时任务信息分页列表
     * @param scheduleGroup 定时任务组 device：设备物模型  camera：视频监控  database:数据库  monitor：监控中心
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getSchedulePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getSchedulePageList(@RequestParam(name="scheduleGroup", defaultValue="") String scheduleGroup,
                                    @RequestParam(name="key", defaultValue="") String key,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Schedule> example = createExample(scheduleGroup, key);
            IPage<Schedule> page = scheduleService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Schedule> createExample(String scheduleGroup, String key) {
        QueryWrapper<Schedule> example = new QueryWrapper<>();
        if(StrUtil.isEmpty(scheduleGroup)) {
            example.eq("schedule_group", scheduleGroup);
        }
        if(!StrUtil.isEmpty(key)) {
            example.like("schedule_name", key);
        }
        example.orderByDesc("create_time");
        return example;
    }

    /**
     * 获取定时任务信息
     * @param id id
     */
    @GetMapping("/getSchedule")
    @PublicHeadLimit
    @ResponseBody
    public void getSchedule(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(scheduleService.existsNotScheduleId(id)) {
            jsonObject = ResponseUtil.getErrorJson("定时任务不存在");
        } else {
            Schedule data = scheduleService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增定时任务信息
     * @param record 定时任务信息
     */
    @PostMapping("/addSchedule")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addSchedule(@Validated(Add.class) @Valid Schedule record) {
        JSONObject jsonObject;
        try {
            scheduleService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "定时任务", "新增", "新增定时任务信息，【名称】" + record.getScheduleName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑定时任务信息
     * @param record 定时任务信息
     */
    @PostMapping("/editSchedule")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editSchedule(@Validated(Edit.class) @Valid Schedule record) {
        JSONObject jsonObject;
        try {
            scheduleService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "定时任务", "修改", "修改定时任务信息，【名称】" + record.getScheduleName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改状态
     * @param scheduleStatus 提交数据
     */
    @GetMapping("/updateStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateStatus(@Validated(Edit.class) @Valid ScheduleStatus scheduleStatus) {
        JSONObject jsonObject;
        try {
            Schedule schedule = scheduleService.updateStatus(scheduleStatus);
            String scheduleName = schedule.getScheduleName();

            String option = "停止";
            if(scheduleStatus.getStatus() == 2) {
                option = "正常";
            } else if(scheduleStatus.getStatus() == 3) {
                option = "异常";
            }

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "定时任务", "修改", "定时任务状态" + option + "，【名称】" + scheduleName);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }


    /**
     * 删除定时任务信息
     * @param id id
     */
    @GetMapping("/delSchedule")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSchedule(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!scheduleService.existsNotScheduleId(id)) {
                Schedule schedule = scheduleService.find(id);
                String scheduleName = schedule.getScheduleName();
                int count = scheduleService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "定时任务", "删除", "删除定时任务信息，【名称】" + scheduleName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个定时任务信息
     * @param ids id集合
     */
    @GetMapping("/delSchedules")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSchedules(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = scheduleService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "定时任务", "删除", "批量删除" + count + "条定时任务");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
