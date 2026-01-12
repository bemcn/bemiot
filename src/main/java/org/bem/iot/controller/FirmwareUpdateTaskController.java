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
import org.bem.iot.model.general.FirmwareUpdateTask;
import org.bem.iot.service.FirmwareUpdateTaskService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 固件升级计划
 * @author jakybland
 */
@RestController
@RequestMapping("/firmware_task")
public class FirmwareUpdateTaskController {
    @Resource
    FirmwareUpdateTaskService firmwareUpdateTaskService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取升级计划分页列表
     * @param firmwareId 固件ID
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getFirmwareUpdateTaskPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareUpdateTaskPageList(@RequestParam(name="firmwareId", defaultValue="0") int firmwareId,
                                              @RequestParam(name="key", defaultValue="") String key,
                                              @RequestParam(name="index", defaultValue = "1") Integer index,
                                              @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            if(firmwareId == 0) {
                jsonObject = ResponseUtil.getErrorJson("固件ID不能为空");
            } else {
                QueryWrapper<FirmwareUpdateTask> example = new QueryWrapper<>();
                example.eq("firmwareId", firmwareId);
                if(StrUtil.isNotEmpty(key)) {
                    example.like("task_name", key);
                }
                example.orderByDesc("create_time");
                IPage<FirmwareUpdateTask> page = firmwareUpdateTaskService.selectPage(example, index, size);

                jsonObject = ResponseUtil.getSuccessJson(page);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取升级计划
     * @param id 计划id
     */
    @GetMapping("/getFirmwareUpdateTask")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareUpdateTask(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id == 0) {
            jsonObject = ResponseUtil.getErrorJson("计划id不能为空");
        } else if(firmwareUpdateTaskService.existsNotFirmwareTaskId(id)) {
            jsonObject = ResponseUtil.getErrorJson("计划信息不存在");
        } else {
            FirmwareUpdateTask data = firmwareUpdateTaskService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增升级计划
     * @param record 升级计划
     */
    @PostMapping("/addFirmwareUpdateTask")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addFirmwareUpdateTask(@Validated @Valid FirmwareUpdateTask record) {
        JSONObject jsonObject;
        try {
            firmwareUpdateTaskService.insert(record);
            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "固件信息", "新增", "新增固件升级计划，【名称】" + record.getTaskName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除升级计划
     * @param id 计划id
     */
    @GetMapping("/delFirmwareUpdateTask")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delFirmwareUpdateTask(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!firmwareUpdateTaskService.existsNotFirmwareTaskId(id)) {
                FirmwareUpdateTask firmwareUpdateTask = firmwareUpdateTaskService.find(id);
                String taskName = firmwareUpdateTask.getTaskName();
                int count = firmwareUpdateTaskService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "固件信息", "删除", "删除升级计划，【名称】" + taskName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
