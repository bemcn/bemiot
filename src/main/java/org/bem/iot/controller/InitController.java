package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.entity.ReturnToken;
import org.bem.iot.service.AppAuthService;
import org.bem.iot.service.LoginService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 系统服务
 * @author jaky
 */
@RestController
@RequestMapping("/")
public class InitController {
    @Resource
    AppAuthService appAuthService;

    @Resource
    LoginService loginService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取系统AccessToken
     * @param appId 运用ID
     */
    @GetMapping("/getAccessToken")
    @ResponseBody
    public void getAccessToken(@RequestParam(name="appId", defaultValue="") String appId) {
        JSONObject jsonObject;
        try {
            if(appAuthService.existsNotApp(appId, "system")) {
                jsonObject = ResponseUtil.getErrorJson("无效的运用ID");
            } else {
                String accessToken = loginService.systemAccessToken(appId);
                JSONObject obj = new JSONObject();
                obj.put("access_token", accessToken);
                obj.put("timestamp", 7200);
                jsonObject = ResponseUtil.getSuccessJson(obj);
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 验证系统AccessToken
     * @param accessToken 系统AccessToken
     */
    @GetMapping("/existAccessToken")
    @ResponseBody
    public void existAccessToken(@RequestParam(name="accessToken", defaultValue="") String accessToken) {
        JSONObject jsonObject;
        if(loginService.verifySystemToken(accessToken)) {
            jsonObject = ResponseUtil.getSuccessJson(true);
        } else {
            jsonObject = ResponseUtil.getSuccessJson(false);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 刷新AccessToken
     * @param refreshToken 用于刷新的refreshToken
     */
    @PostMapping("/refreshAccessToken")
    @ResponseBody
    public void refreshAccessToken(@RequestParam(name="refreshToken", defaultValue="") String refreshToken) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(refreshToken)) {
            jsonObject = ResponseUtil.getErrorJson("refreshToken不能为空");
        } else {
            ReturnToken returnToken = loginService.refreshAccessToken(refreshToken);
            if(returnToken.getStatus()) {
                JSONObject obj = new JSONObject();
                obj.put("accessToken", returnToken.getAccessToken());
                obj.put("refreshToken", returnToken.getRefreshToken());
                obj.put("timestamp", 7200);
                jsonObject = ResponseUtil.getSuccessJson(obj);
            } else {
                jsonObject = ResponseUtil.getErrorJson("AccessToken刷新失败");
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 验证AccessToken
     * @param accessToken 当前用户accessToken
     */
    @GetMapping("/verifyToken")
    @ResponseBody
    public void verifyToken(@RequestParam(name="accessToken", defaultValue="") String accessToken) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(accessToken)) {
            jsonObject = ResponseUtil.getErrorJson("accessToken不能为空");
        } else {
            if(loginService.verifyAccessToken(accessToken)) {
                jsonObject = ResponseUtil.getSuccessJson(true);
            } else {
                jsonObject = ResponseUtil.getSuccessJson(false);
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
