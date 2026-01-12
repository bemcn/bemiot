package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.entity.Login;
import org.bem.iot.entity.LoginUser;
import org.bem.iot.entity.UserLogin;
import org.bem.iot.model.log.LogSystem;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.service.AppAuthService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.LoginService;
import org.bem.iot.util.InternalIdUtil;
import org.bem.iot.util.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录
 * @author jaky
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    AppAuthService appAuthService;

    @Resource
    LoginService loginService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取登录Code
     * @param appId 运用ID
     */
    @GetMapping(value = "/getLoginCode")
    @ResponseBody
    public void getLoginCode(@RequestParam(name="appId", defaultValue="") String appId) {
        JSONObject jsonObject;
        if(appAuthService.existsNotApp(appId, "user")) {
            jsonObject = ResponseUtil.getErrorJson("无效的运用ID");
        } else {
            String code = loginService.getLoginCode(appId);
            jsonObject = ResponseUtil.getSuccessJson(code);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 用户登录验证
     * @param login 登录信息
     */
    @PostMapping("/chkLogin")
    @ResponseBody
    public void chkLogin(@Validated @Valid Login login) {
        JSONObject jsonObject;
        try {
            String ipAddress = ResponseUtil.getIpAddress(request);

            UserLogin loginData = loginService.checkLogin(login, ipAddress);
            if (loginData.isStatus()) {
                UserInfo userInfo = loginData.getUser();
                LoginUser user = new LoginUser();
                user.setUserName(userInfo.getUserName());
                user.setRoleName(userInfo.getRole().getRoleName());
                user.setNickName(userInfo.getNickName());
                user.setHeadImg(userInfo.getHeadImg());
                String strUser = JSON.toJSONString(user);
                JSONObject userJson = JSONObject.parseObject(strUser);

                String roleAuth = userInfo.getRole().getRoleAuth();
                LogSystem log = createLogSystem(loginData, ipAddress, userInfo);
                try {
                    logSystemService.insert(log);
                } catch (Exception e) {
                    System.out.println("添加日志失败:" + e.getMessage());
                }

                JSONObject obj = new JSONObject();
                obj.put("user", userJson);
                obj.put("permissions", roleAuth);
                obj.put("accessToken", loginData.getAccessToken());
                obj.put("refreshToken", loginData.getRefreshToken());
                obj.put("timestamp", 7200);

                jsonObject = ResponseUtil.getSuccessJson(obj);
            } else {
                jsonObject = ResponseUtil.getErrorJson(loginData.getMessage());
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 用户退出登录
     * @param refreshToken 用户refreshToke
     */
    @PostMapping("/loginOut")
    @ResponseBody
    public void loginOut(@RequestParam(name="refreshToken", defaultValue="") String refreshToken) {
        JSONObject jsonObject;
        if(!StrUtil.isEmpty(refreshToken)) {
            loginService.deleteAccessToken(refreshToken);
        }
        jsonObject = ResponseUtil.getSuccessJson();
        ResponseUtil.responseData(jsonObject, response);
    }

    private static LogSystem createLogSystem(UserLogin loginData, String ipAddress, UserInfo user) {
        String id = InternalIdUtil.createId();

        LogSystem log = new LogSystem();
        log.setTs(System.currentTimeMillis());
        log.setLogId(id);
        log.setClientSource(loginData.getClientSource());
        log.setClientIp(ipAddress);
        log.setUserId(user.getUserId());
        log.setUserName(user.getUserName());
        log.setNickName(user.getNickName());
        log.setModelName("用户登录");
        log.setOperation("验证");
        log.setDescription("用户登录验证成功");
        return log;
    }
}
