package org.bem.iot.config.head;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.service.AppAuthService;
import org.bem.iot.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 公共头拦截
 * @author JiangShiYi
 *
 */
@Component
public class HeadInterceptor implements HandlerInterceptor {
	@Resource
	AppAuthService appAuthService;

	@Resource
	LoginService loginService;

	@Override
	public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
		int erroCode = 200;
		String erroMsg = "";

		if ((handler instanceof HandlerMethod method)) {
			PublicHeadLimit permission = method.getMethodAnnotation(PublicHeadLimit.class);
			//如果配置了注解
			if (permission != null ) {
				String limitVal = permission.value();
				if(StrUtil.isEmpty(limitVal)) {
					limitVal = "all";
				}
				boolean authRequest;
				String type = request.getHeader("type");

				if (StrUtil.isEmpty(type)) {
					authRequest = false;
				} else {
					if ("all".equals(limitVal)) {
						authRequest = true;
					} else {
						authRequest = type.equals(limitVal);
					}
				}

				if(authRequest) {
					//获取公共头
					String appId = request.getHeader("appId");
					String accessToken = request.getHeader("accessToken");
					String signMethod = request.getHeader("signMethod");
					String sign = request.getHeader("sign");
					String timestamp = request.getHeader("timestamp");
					String v = request.getHeader("v");

					if (StrUtil.isEmpty(appId)) {
						erroCode = 1000;
                        erroMsg = "运用ID不能为空";
					} else if (StrUtil.isEmpty(type)) {
						erroCode = 1000;
                        erroMsg = "类型不能为空";
					} else if (StrUtil.isEmpty(accessToken)) {
						erroCode = 1000;
                        erroMsg = "Access Token不能为空";
					} else if (StrUtil.isEmpty(signMethod)) {
						erroCode = 1000;
                        erroMsg = "签名加密方式不能为空";
					} else if (StrUtil.isEmpty(sign)) {
						erroCode = 1000;
                        erroMsg = "签名不能为空";
					} else if (StrUtil.isEmpty(timestamp)) {
						erroCode = 1000;
                        erroMsg = "时间戳不能为空";
					} else if (StrUtil.isEmpty(v)) {
						erroCode = 1000;
                        erroMsg = "API内部版本号不能为空";
					} else if (!appAuthService.existsType(type)) {
						erroCode = 1001;
                        erroMsg = "类型提交错误";
					} else if (appAuthService.existsNotApp(appId, type)) {
						erroCode = 1001;
                        erroMsg = "运用ID错误，未授权";
					} else if (loginService.existsNotAccessToken(accessToken, type)) {
						erroCode = 1060;
						erroMsg = "用户Access Token失效不存在";
					} else if (loginService.verifyNotTimestamp(timestamp, sign)) {
						erroCode = 1060;
						erroMsg = "时间戳超时无效";
					} else if (loginService.verifyNotVersion(v)) {
						erroCode = 1060;
                        erroMsg = "API内部版本号错误";
					} else if (loginService.verifyNotSign(request)) {
						erroCode = 1002;
                        erroMsg = "签名验证错误";
					}
				} else {
					erroCode = 1005;
					erroMsg = "无请求权限";
				}
			}
		}
		if(erroCode == 200) {
			return HandlerInterceptor.super.preHandle(request, response, handler);
		} else {
            String contentType = "application/json";

            JSONObject jSONObject = new JSONObject();
			jSONObject.put("status", "fail");
			jSONObject.put("code", erroCode);
			jSONObject.put("message", erroMsg);

			response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setHeader("Cache-Control", "no-cache");
		    response.setCharacterEncoding("UTF-8");
		    response.setContentType(contentType);
		    response.getWriter().println(jSONObject.toJSONString());
		    response.getWriter().flush();
			return false;
		}
	}
}
