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
import org.bem.iot.model.device.DeviceUser;
import org.bem.iot.service.DeviceUserService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设备用户权限用户权限
 * @author jakybland
 */
@RestController
@RequestMapping("/device_user")
public class DeviceUserController {
    @Resource
    DeviceUserService deviceUserService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备用户权限分页列表
     * @param deviceId 设备ID
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceUserPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceUserPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                      @RequestParam(name="index", defaultValue = "1") Integer index,
                                      @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        if(StrUtil.isNotEmpty(deviceId)) {
            jsonObject = ResponseUtil.getErrorJson("设备ID不能为空或小于0");
        } else {
            try {
                QueryWrapper<DeviceUser> example = new QueryWrapper<>();
                example.eq("device_id", deviceId);
                example.orderByDesc("create_time");
                IPage<DeviceUser> page = deviceUserService.selectPage(example, index, size);

                jsonObject = ResponseUtil.getSuccessJson(page);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备用户权限
     * @param id id
     */
    @GetMapping("/getDeviceUser")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceUser(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(deviceUserService.existsNotDeviceUserId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备用户权限信息不存在");
        } else {
            DeviceUser data = deviceUserService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增设备用户权限
     * @param record 设备用户权限
     */
    @PostMapping("/addDeviceUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDeviceUser(@Validated(Add.class) @Valid DeviceUser record) {
        JSONObject jsonObject;
        try {
            DeviceUser data = deviceUserService.find(record.getDeviceUserId());
            String deviceName = data.getDevice().getDeviceName();
            String userName = data.getUser().get("userName").toString();
            deviceUserService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备用户权限", "新增", "新增设备【 "+ deviceName +" 】绑定用户【" + userName + "】的权限");
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑设备用户权限
     * @param record 设备用户权限
     */
    @PostMapping("/editDeviceUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDeviceUser(@Validated(Edit.class) @Valid DeviceUser record) {
        JSONObject jsonObject;
        try {
            DeviceUser data = deviceUserService.find(record.getDeviceUserId());
            String deviceName = data.getDevice().getDeviceName();
            String userName = data.getUser().get("userName").toString();
            deviceUserService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备用户权限", "修改", "修改设备【 "+ deviceName +" 】绑定用户【" + userName + "】的权限");

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除设备用户权限
     * @param id id
     */
    @GetMapping("/delDeviceUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceUser(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        try {
            if(!deviceUserService.existsNotDeviceUserId(id)) {
                DeviceUser data = deviceUserService.find(id);
                String deviceName = data.getDevice().getDeviceName();
                String userName = data.getUser().get("userName").toString();
                int count = deviceUserService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备用户权限", "删除", "删除设备【 "+ deviceName +" 】绑定用户【" + userName + "】的权限");
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个设备用户权限
     * @param ids id集合
     */
    @GetMapping("/delDeviceUsers")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceUsers(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
                int count = deviceUserService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备用户权限", "删除", "批量删除" + count + "条设备用户权限信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
