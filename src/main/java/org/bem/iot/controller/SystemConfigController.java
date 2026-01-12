package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.SystemConfigService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统参数
 * @author jakybland
 */
@RestController
@RequestMapping("/conf")
public class SystemConfigController {
    @Resource
    SystemConfigService systemConfigService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取字典信息列表
     * @param group 分组标识
     */
    @GetMapping("/getConfigParams")
    @PublicHeadLimit
    @ResponseBody
    public void getConfigParams(@RequestParam(name="group", defaultValue = "") String group) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(group)) {
            jsonObject = ResponseUtil.getErrorJson("请输入分组标识");
        } else {
            try {
                Map<String, String> map = systemConfigService.selectByGroup(group);
                jsonObject = ResponseUtil.getSuccessJson(map);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取指定KEY的值
     * @param key key
     */
    @GetMapping("/getConfigValue")
    @PublicHeadLimit
    @ResponseBody
    public void getConfigValue(@RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            String value = systemConfigService.find(key);
            Map<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("value", value);
            jsonObject = ResponseUtil.getSuccessJson(map);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取多个KEY的值
     * @param keys key集合
     */
    @GetMapping("/getConfigValues")
    @PublicHeadLimit
    @ResponseBody
    public void getConfigValues(@RequestParam(name="keys", defaultValue="") String keys) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(keys)) {
            jsonObject = ResponseUtil.getErrorJson("Key键集合不能为空");
        } else {
            try {
                String[] keysArray = keys.split(",");
                Map<String, String> map = systemConfigService.findArray(keysArray);
                jsonObject = ResponseUtil.getSuccessJson(map);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑配置参数
     * @param record 配置参数
     */
    @PostMapping("/editConfig")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editConfig(@RequestParam Map<String, Object> record) {
        JSONObject jsonObject;
        try {
            JSONObject recordJson = new JSONObject(record);
            String group = recordJson.getString("group");
            String groupName = switch (group) {
                case "basic" -> "基础参数";
                case "parameter" -> "业务参数";
                case "safe" -> "安全设置";
                case "wechat" -> "微信消息";
                case "sms" -> "短信消息";
                case "mail" -> "邮件消息";
                default -> "内置参数";
            };
            systemConfigService.updateByGroup(recordJson);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "系统参数", "修改", "修改系统参数，【分组】" + groupName);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
