package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.AppAuthParams;
import org.bem.iot.model.general.AppAuth;
import org.bem.iot.service.AppAuthService;
import org.bem.iot.service.CacheableTimerService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.InternalIdUtil;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 运用信息
 * @author jaky
 */
@RestController
@RequestMapping("/auth")
public class AppAuthController {
    @Resource
    AppAuthService appAuthService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    CacheableTimerService cacheableTimerService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取运用授权分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getAppAuthPageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getAppAuthPageList(@RequestParam(name="key", defaultValue="") String key,
                               @RequestParam(name="index", defaultValue = "1") Integer index,
                               @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<AppAuth> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("app_name", key);
            }
            example.orderByDesc("create_time");
            IPage<AppAuth> page = appAuthService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取运用信息
     * @param appId 运用ID
     */
    @GetMapping("/getAppAuth")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getAppAuth(@RequestParam(name="appId", defaultValue="") String appId) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(appId)) {
            jsonObject = ResponseUtil.getErrorJson("运用ID不能为空");
        } else if(appAuthService.existsNotApp(appId)) {
            jsonObject = ResponseUtil.getErrorJson("运用ID提交错误");
        } else {
            AppAuth data = appAuthService.find(appId);
            cacheableTimerService.setAppAuthCacheWithCustomExpire("app_auth", appId);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增运用
     * @param record 运用信息
     */
    @PostMapping("/addAppAuth")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addAppAuth(@Validated @Valid AppAuthParams record) {
        JSONObject jsonObject;
        try {
            String appId = InternalIdUtil.createAppId();
            String secretKey = InternalIdUtil.createSecretKey();
            JSONObject obj = JSONObject.from(record);
            obj.put("appId", appId);
            obj.put("secretKey", secretKey);
            obj.put("isSystem", 0);
            obj.put("createTime", new Date());
            AppAuth appAuth = JSON.parseObject(obj.toString(), AppAuth.class);

            appAuthService.insert(appAuth);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "运用授权", "新增", "新增运用授权，【AppId】" + appId);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑运用
     * @param record 运用信息
     */
    @PostMapping("/editAppAuth")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editAppAuth(@Validated @Valid AppAuthParams record) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(record.getAppId())) {
                jsonObject = ResponseUtil.getErrorJson("应用ID不能为空");
            } else if(appAuthService.existsNotApp(record.getAppId())) {
                jsonObject = ResponseUtil.getErrorJson("应用ID提交错误");
            } else {
                String appId = record.getAppId();
                AppAuth appAuth = appAuthService.find(appId);
                appAuth.setAppName(record.getAppName());
                appAuth.setAppSource(record.getAppSource());
                appAuth.setAppEnvironment(record.getAppEnvironment());
                appAuth.setAppAuth(record.getAppAuth());
                appAuth.setSecureType(record.getSecureType());
                appAuth.setAesKey(record.getAesKey());
                appAuth.setAesIv(record.getAesIv());
                appAuth.setPublicKey(record.getPublicKey());
                appAuth.setPrivateKey(record.getPrivateKey());
                appAuth.setRemark(record.getRemark());

                appAuthService.update(appAuth);
                cacheableTimerService.setAppAuthCacheWithCustomExpire("app_auth", appId);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "运用授权", "修改", "修改运用授权，【AppId】" + appId);

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 更换secretKey
     * @param appId 运用ID
     */
    @GetMapping("/replaceSecretKey")
    @PublicHeadLimit("user")
    @ResponseBody
    public void replaceSecretKey(@RequestParam(name="appId", defaultValue="") String appId) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(appId)) {
                jsonObject = ResponseUtil.getErrorJson("应用ID不能为空");
            } else if(appAuthService.existsNotApp(appId)) {
                jsonObject = ResponseUtil.getErrorJson("应用ID提交错误");
            } else {
                String secretKey = InternalIdUtil.createSecretKey();
                AppAuth appAuth = appAuthService.find(appId);
                appAuth.setSecretKey(secretKey);

                appAuthService.update(appAuth);
                cacheableTimerService.setAppAuthCacheWithCustomExpire("app_auth", appId);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "运用授权", "修改", "修改运用授权SecretKey，【AppId】" + appId);

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除运用
     * @param appId 运用ID
     */
    @GetMapping("/delAppAuth")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delAppAuth(@RequestParam(name="appId", defaultValue="") String appId) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(appId)) {
                jsonObject = ResponseUtil.getErrorJson("应用ID不能为空");
            } else if(appAuthService.existsNotApp(appId)) {
                jsonObject = ResponseUtil.getErrorJson("应用ID提交错误");
            } else {
                int count = appAuthService.del(appId);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "运用授权", "删除", "删除运用授权，【AppId】" + appId);
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个运用
     * @param appIds 运用ID集合
     */
    @GetMapping("/delAppAuths")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delAppAuths(@RequestParam(name="appIds", defaultValue="") String appIds) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(appIds)) {
                jsonObject = ResponseUtil.getErrorJson("应用ID集合不能为空");
            } else {
                List<String> appIdList = Arrays.asList(appIds.split(","));
                int count = appAuthService.delArray(appIdList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "运用授权", "删除", "批量删除" + count + "条运用授权信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
