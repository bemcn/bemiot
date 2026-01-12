package org.bem.iot.util;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResponseUtil {
    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

	public static String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		} else {
			// 可能会有多个IP，取第一个逗号前的IP
			int index = ipAddress.indexOf(',');
			if (index != -1) {
				ipAddress = ipAddress.substring(0, index);
			}
		}
		return ipAddress;
	}

	public static <T> JSONObject getSuccessJson() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "success");
		jsonObj.put("code", 200);
		jsonObj.put("message", "ok");
		return jsonObj;
	}

	public static <T> JSONObject getSuccessJson(int code, String message) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "success");
		jsonObj.put("code", code);
		jsonObj.put("message", message);
		return jsonObj;
	}

	public static <T> JSONObject getSuccessJson(T data) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "success");
		jsonObj.put("code", 200);
		jsonObj.put("message", "ok");
		jsonObj.put("data", data);
		return jsonObj;
	}

	public static JSONObject getErrorJson(String message) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "fail");
		jsonObj.put("code", 200);
		jsonObj.put("message", message);
		return jsonObj;
	}

	public static JSONObject getErrorJson(int code, String message) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "fail");
		jsonObj.put("code", code);
		jsonObj.put("message", message);
		return jsonObj;
	}

	public static <T> JSONObject getErrorJson(int code, String message, T data) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "fail");
		jsonObj.put("code", code);
		jsonObj.put("message", message);
		jsonObj.put("data", data);
		return jsonObj;
	}
	
	public static void responseData(JSONObject obj, HttpServletResponse response) {
		String responseBody = obj.toJSONString();
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    try {
			response.getWriter().println(responseBody);
			response.getWriter().flush();
		} catch (IOException e) {
	        logger.error(e.getMessage());
		}
	}
}
