package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.DriveParams;
import org.bem.iot.service.DriveParamsService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 驱动参数
 * @author jakybland
 */
@RestController
@RequestMapping("/drive_params")
public class DriveParamsController {
    @Resource
    DriveParamsService driveParamsService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取驱动参数列表
     * @param type 分组标识 1：产品信息 2：设备信息 3：物模型
     */
    @GetMapping("/getDriveParamsList")
    @PublicHeadLimit
    @ResponseBody
    public void getDriveParamsList(@RequestParam(name="driveCode", defaultValue="") String driveCode,
                                   @RequestParam(name="type", defaultValue="0") int type) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(driveCode)) {
                jsonObject = ResponseUtil.getErrorJson("请求参数错误");
            } else if(type < 1 || type > 3) {
                jsonObject = ResponseUtil.getErrorJson("请求参数错误");
            } else {
                List<DriveParams> list = driveParamsService.select(driveCode, type);
                jsonObject = ResponseUtil.getSuccessJson(list);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取驱动参数
     * @param id 参数id
     */
    @GetMapping("/getDriveParams")
    @PublicHeadLimit
    @ResponseBody
    public void getDriveParams(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(driveParamsService.existsNotParamsId(id)) {
            jsonObject = ResponseUtil.getErrorJson("参数信息不存在");
        } else {
            DriveParams data = driveParamsService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增驱动参数
     * @param record 驱动参数
     */
    @PostMapping("/addDriveParams")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDriveParams(@Validated(Add.class) @Valid DriveParams record) {
        JSONObject jsonObject;
        try {
            if (driveParamsService.existsParamsKey(record.getDriveCode(), record.getParamsKey())) {
                jsonObject = ResponseUtil.getErrorJson("参数标识已存在");
            } else {
                driveParamsService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "新增", "新增驱动参数，【参数名称】" + record.getParamsName());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑驱动参数
     * @param record 驱动参数
     */
    @PostMapping("/editDriveParams")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDriveParams(@Validated(Edit.class) @Valid DriveParams record) {
        JSONObject jsonObject;
        try {
            driveParamsService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "驱动信息", "修改", "修改驱动参数，【参数名称】" + record.getParamsName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改分组排序
     * @param id 参数id
     */
    @GetMapping("/updateOrderNumber")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNumber(@RequestParam(name="id", defaultValue="") long id, @RequestParam(name="orderNum", defaultValue="0") int orderNum) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(driveParamsService.existsNotParamsId(id)) {
            jsonObject = ResponseUtil.getErrorJson("参数信息不存在");
        } else {
            try {
                DriveParams data = driveParamsService.find(id);
                String paramsName = data.getParamsName();
                driveParamsService.updateOrder(id, orderNum);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "驱动信息", "修改", "修改驱动参数排序为" + orderNum + "，【参数名称】" + paramsName);
                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除驱动参数
     * @param id 参数id
     */
    @GetMapping("/delDriveParams")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDriveParams(@RequestParam(name="id", defaultValue="") long id) {
        JSONObject jsonObject;
        try {
            if(!driveParamsService.existsNotParamsId(id)) {
                DriveParams data = driveParamsService.find(id);
                String paramsName = data.getParamsName();
                int count = driveParamsService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "驱动信息", "删除", "删除驱动参数，【参数名称】" + paramsName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个驱动参数
     * @param ids 参数id集合
     */
    @GetMapping("/delDriveParamss")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDriveParamss(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
                int count = driveParamsService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "驱动信息", "删除", "批量删除" + count + "条驱动参数信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
