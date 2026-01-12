package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.FirmwareUpdateLog;
import org.bem.iot.service.FirmwareUpdateLogService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 固件升级日志
 * @author jakybland
 */
@RestController
@RequestMapping("/firmware_log")
public class FirmwareUpdateLogController {
    @Resource
    FirmwareUpdateLogService firmwareUpdateLogService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取升级计划统计
     * @param firmwareId 固件ID
     */
    @GetMapping("/getFirmwareUpdateTotal")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareUpdateTotal(@RequestParam(name="firmwareId", defaultValue="0") int firmwareId) {
        JSONObject jsonObject;
        try {
            if(firmwareId == 0) {
                jsonObject = ResponseUtil.getErrorJson("固件ID不能为空");
            } else {
                JSONObject data = firmwareUpdateLogService.selectTotal(firmwareId);
                jsonObject = ResponseUtil.getSuccessJson(data);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取升级日志分页列表
     * @param taskId 计划ID
     * @param key 设备编号关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getFirmwareUpdateLogPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getFirmwareUpdateLogPageList(@RequestParam(name="taskId", defaultValue="0") int taskId,
                                             @RequestParam(name="key", defaultValue = "") String key,
                                             @RequestParam(name="index", defaultValue = "1") Integer index,
                                             @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            if(taskId == 0) {
                jsonObject = ResponseUtil.getErrorJson("固件ID不能为空");
            } else {
                QueryWrapper<FirmwareUpdateLog> example = new QueryWrapper<>();
                example.eq("task_id", taskId);
                if(StrUtil.isNotEmpty(key)) {
                    example.like("device_id", key);
                }
                example.orderByDesc("update_time");
                IPage<FirmwareUpdateLog> page = firmwareUpdateLogService.selectPage(example, index, size);

                jsonObject = ResponseUtil.getSuccessJson(page);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增升级日志
     * @param record 升级日志
     */
    @PostMapping("/addFirmwareUpdateLog")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addFirmwareUpdateLog(@Validated @Valid FirmwareUpdateLog record) {
        JSONObject jsonObject;
        try {
            firmwareUpdateLogService.insertOrUpdate(record);
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
