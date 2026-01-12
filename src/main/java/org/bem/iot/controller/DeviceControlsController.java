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
import org.bem.iot.model.device.DeviceControls;
import org.bem.iot.service.DeviceControlsService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设备群控
 * @author jakybland
 */
@RestController
@RequestMapping("/device_controls")
public class DeviceControlsController {
    @Resource
    DeviceControlsService deviceControlsService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备群控分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceControlsPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceControlsPageList(@RequestParam(name="key", defaultValue="") String key,
                                          @RequestParam(name="index", defaultValue = "1") Integer index,
                                          @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DeviceControls> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("control_name", key);
            }
            example.orderByDesc("create_time");
            IPage<DeviceControls> page = deviceControlsService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备群控
     * @param id id
     */
    @GetMapping("/getDeviceControls")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceControls(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(deviceControlsService.existsNotControlId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备群控不存在");
        } else {
            DeviceControls data = deviceControlsService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增设备群控
     * @param record 设备群控
     */
    @PostMapping("/addDeviceControls")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDeviceControls(@Validated(Add.class) @Valid DeviceControls record) {
        JSONObject jsonObject;
        try {
            deviceControlsService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备群控", "新增", "新增设备群控规则，【名称】" + record.getControlName());
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑设备群控
     * @param record 设备群控
     */
    @PostMapping("/editDeviceControls")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDeviceControls(@Validated(Edit.class) @Valid DeviceControls record) {
        JSONObject jsonObject;
        try {
            deviceControlsService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备群控", "修改", "修改设备群控规则，【名称】" + record.getControlName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除设备群控
     * @param id id
     */
    @GetMapping("/delDeviceControls")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceControls(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        try {
            if(!deviceControlsService.existsNotControlId(id)) {
                DeviceControls device = deviceControlsService.find(id);
                String controlName = device.getControlName();
                int count = deviceControlsService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备群控", "删除", "删除设备群控规则，【名称】" + controlName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个设备群控
     * @param ids id集合
     */
    @GetMapping("/delDeviceControlss")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceControlss(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
                int count = deviceControlsService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备群控", "删除", "批量删除" + count + "条设备群控规则");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
