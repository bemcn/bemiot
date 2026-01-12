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
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.ModelAlarmRules;
import org.bem.iot.service.DeviceService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.ModelAlarmRulesService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 物模型告警规则
 * @author jakybland
 */
@RestController
@RequestMapping("/modelAlarmRules")
public class ModelAlarmRulesController {
    @Resource
    ModelAlarmRulesService alarmRulesService;

    @Resource
    DeviceService deviceService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取物模型告警规则列表
     * @param deviceId 设备ID
     * @param productId 物模型告警规则ID
     * @param identity 物模型标识
     * @param level 告警等级 1：一般告警 2：重要告警 3：紧急告警
     * @param key 关键字
     */
    @GetMapping("/getModelAlarmRulesList")
    @PublicHeadLimit
    @ResponseBody
    public void getModelAlarmRulesList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                       @RequestParam(name="productId", defaultValue="") String productId,
                                       @RequestParam(name="identity", defaultValue="") String identity,
                                       @RequestParam(name="level", defaultValue="0") Integer level,
                                       @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<ModelAlarmRules> example = new QueryWrapper<>();
            if(StrUtil.isNotEmpty(deviceId)) {
                Device device = deviceService.findMeta(deviceId);
                productId = device.getProductId(); 
                example.eq("product_id", productId);
            }
            if(StrUtil.isNotEmpty(productId)) {
                example.eq("product_id", productId);
            }
            if(StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if(level > 0) {
                example.eq("alarm_level", level);
            }
            if(!StrUtil.isEmpty(key)) {
                example.like("rules_name", key);
            }
            example.orderByDesc("rules_id");
            List<ModelAlarmRules> list = alarmRulesService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取物模型告警规则分页列表
     * @param deviceId 设备ID
     * @param productId 物模型告警规则ID
     * @param identity 物模型标识
     * @param level 告警等级 1：紧急告警 2：重要告警 3：一般告警
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getModelAlarmRulesPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getModelAlarmRulesPageList(@RequestParam(name="deviceId", defaultValue="") String deviceId,
                                           @RequestParam(name="productId", defaultValue="") String productId,
                                           @RequestParam(name="identity", defaultValue="") String identity,
                                           @RequestParam(name="level", defaultValue="0") Integer level,
                                           @RequestParam(name="key", defaultValue="") String key,
                                           @RequestParam(name="index", defaultValue = "1") Integer index,
                                           @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<ModelAlarmRules> example = new QueryWrapper<>();
            if(StrUtil.isNotEmpty(deviceId)) {
                Device device = deviceService.findMeta(deviceId);
                productId = device.getProductId();
                example.eq("product_id", productId);
            }
            if(StrUtil.isNotEmpty(productId)) {
                example.eq("product_id", productId);
            }
            if(StrUtil.isNotEmpty(identity)) {
                example.eq("model_identity", identity);
            }
            if(level > 0) {
                example.eq("alarm_level", level);
            }
            if(!StrUtil.isEmpty(key)) {
                example.like("rules_name", key);
            }
            example.orderByDesc("rules_id");
            IPage<ModelAlarmRules> page = alarmRulesService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取物模型告警规则
     * @param id id
     */
    @GetMapping("/getModelAlarmRules")
    @PublicHeadLimit
    @ResponseBody
    public void getModelAlarmRules(@RequestParam(name="id", defaultValue="0") Long id) {
        JSONObject jsonObject;
        if(id < 1L) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(alarmRulesService.existsNotModelAlarmRulesId(id)) {
            jsonObject = ResponseUtil.getErrorJson("物模型告警规则信息不存在");
        } else {
            ModelAlarmRules data = alarmRulesService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增物模型告警规则
     * @param record 物模型告警规则
     */
    @PostMapping("/addModelAlarmRules")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addModelAlarmRules(@Validated(Add.class) @Valid ModelAlarmRules record) {
        JSONObject jsonObject;
        try {
            alarmRulesService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "告警规则", "新增", "新增物模型告警规则，【标识】" + record.getModelIdentity());
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑物模型告警规则
     * @param record 物模型告警规则
     */
    @PostMapping("/editModelAlarmRules")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editModelAlarmRules(@Validated(Edit.class) @Valid ModelAlarmRules record) {
        JSONObject jsonObject;
        try {
            alarmRulesService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "告警规则", "修改", "修改物模型告警规则，【标识】" + record.getModelIdentity());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除物模型告警规则
     * @param id id
     */
    @GetMapping("/delModelAlarmRules")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delModelAlarmRules(@RequestParam(name="id", defaultValue="0") Long id) {
        JSONObject jsonObject;
        try {
            if(!alarmRulesService.existsNotModelAlarmRulesId(id)) {
                ModelAlarmRules alarmRules = alarmRulesService.find(id);
                String Identity = alarmRules.getModelIdentity();
                int count = alarmRulesService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "告警规则", "删除", "删除物模型告警规则，【标识】" + Identity);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个物模型告警规则
     * @param ids id集合
     */
    @GetMapping("/delModelAlarmRulesIds")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delModelAlarmRulesIds(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = alarmRulesService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "告警规则", "删除", "批量删除" + count + "条物模型告警规则信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
