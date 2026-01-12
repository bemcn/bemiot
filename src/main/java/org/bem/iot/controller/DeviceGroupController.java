package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.device.DeviceGroup;
import org.bem.iot.service.DeviceGroupService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设备分组
 * @author jakybland
 */
@RestController
@RequestMapping("/device_group")
public class DeviceGroupController {
    @Resource
    DeviceGroupService deviceGroupService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取设备分组列表
     */
    @GetMapping("/getDeviceGroupList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceGroupList() {
        JSONObject jsonObject;
        try {
            List<DeviceGroup> list = deviceGroupService.select();
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备分组分页列表
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDeviceGroupPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceGroupPageList(@RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            IPage<DeviceGroup> page = deviceGroupService.selectPage(index, size);
            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取设备分组
     * @param id 分组id
     */
    @GetMapping("/getDeviceGroup")
    @PublicHeadLimit
    @ResponseBody
    public void getDeviceGroup(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("分组id不能为空或小于1");
        } else if(deviceGroupService.existsNotDeviceGroupId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备分组不存在");
        } else {
            DeviceGroup data = deviceGroupService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增设备分组
     * @param record 设备分组
     */
    @PostMapping("/addDeviceGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDeviceGroup(@Validated(Add.class) @Valid DeviceGroup record) {
        JSONObject jsonObject;
        try {
            deviceGroupService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备分组", "新增", "新增设备分组，【名称】" + record.getGroupName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑设备分组
     * @param record 设备分组
     */
    @PostMapping("/editDeviceGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDeviceGroup(@Validated(Edit.class) @Valid DeviceGroup record) {
        JSONObject jsonObject;
        try {
            deviceGroupService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "设备分组", "修改", "修改设备分组，【名称】" + record.getGroupName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改分组排序
     * @param id 分组id
     */
    @GetMapping("/updateOrderNumber")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNumber(@RequestParam(name="id", defaultValue="") int id, @RequestParam(name="orderNum", defaultValue="0") int orderNum) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(deviceGroupService.existsNotDeviceGroupId(id)) {
            jsonObject = ResponseUtil.getErrorJson("设备分组不存在");
        } else {
            try {
                if (!deviceGroupService.existsNotDeviceGroupId(id)) {
                    DeviceGroup drive = deviceGroupService.find(id);
                    String groupName = drive.getGroupName();
                    deviceGroupService.updateOrder(id, orderNum);

                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备分组", "修改", "修改设备分组排序为" + orderNum + "，【名称】" + groupName);
                }
                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除设备分组
     * @param id 分组id
     */
    @GetMapping("/delDeviceGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceGroup(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!deviceGroupService.existsNotDeviceGroupId(id)) {
                DeviceGroup drive = deviceGroupService.find(id);
                String groupName = drive.getGroupName();
                int count = deviceGroupService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备分组", "删除", "删除设备分组，【名称】" + groupName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个设备分组
     * @param ids 分组id集合
     */
    @GetMapping("/delDeviceGroups")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDeviceGroups(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = deviceGroupService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "设备分组", "删除", "批量删除" + count + "条设备分组");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
