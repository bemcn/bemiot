package org.bem.iot.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Enumeration;

/**
 * HTTP头签名
 * @author JiangShiYi
 *
 */
public class HeadSignUtil {
	/**
	 * 加密方式默认判断类型
	 */
	private static final String METHOD_KEY = "md5";

	/**
	 * 生成签名
	 * @param jsonObject 请求数据,包含Head和Body
	 * @param appKey 运用密钥
	 * @return 返回签名
	 */
	public static String getSign(JSONObject jsonObject, String appKey) {
		String signMethod = jsonObject.getString("signMethod");

		// 对JSONObject的key进行排序
		String[] keys = jsonObject.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		//把所有参数名和参数值串在一起
		StringBuilder strData = new StringBuilder();
		if(METHOD_KEY.equals(signMethod)) {
			strData.append(appKey);
			for (String key : keys) {
				String value = jsonObject.getString(key);
				strData.append(key).append(value.trim());
			}
			strData.append(appKey);
		} else {
			for (String key : keys) {
				String value =jsonObject.getString(key);
				strData.append(key).append(value.trim());
			}
		}
		//使用MD5/HMAC加密
		String sing;
		if(METHOD_KEY.equals(signMethod)) {
			sing = EncryptUtil.encryptMd5By32(strData.toString());
		} else {
			sing = EncryptUtil.encryptHmac(strData.toString(), appKey);
		}
		return sing;
	}

	/**
	 * 获取请求签名数据（路径参数）
	 * @param request 请求对象
	 * @return 返回JSON数据
	 */
	public static JSONObject getSignData(HttpServletRequest request) {
		JSONObject jsonObject = getSignDataByHead(request);

		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if(!"_t".equals(paramName)) {
				String[] paramValues = request.getParameterValues(paramName);
				if (paramValues.length > 0) {
					String paramValue = paramValues[0].trim();
					if (StrUtil.isNotEmpty(paramValue)) {
						if ("appId".equals(paramName) || "accessToken".equals(paramName) || "signMethod".equals(paramName) || "sign".equals(paramName) || "timestamp".equals(paramName) || "v".equals(paramName)) {
							jsonObject.put(paramName + ":x", paramValue);
						} else {
							jsonObject.put(paramName, paramValue);
						}
					}
				}
			}
		}
		return jsonObject;
	}

	/**
	 * 获取请求签名数据（header参数）
	 * @param request 请求对象
	 * @return 返回JSON数据
	 */
	private static JSONObject getSignDataByHead(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appId", request.getHeader("appId"));
		jsonObject.put("type", request.getHeader("type"));
		jsonObject.put("accessToken", request.getHeader("accessToken"));
		jsonObject.put("signMethod", request.getHeader("signMethod"));
		jsonObject.put("timestamp", request.getHeader("timestamp"));
		jsonObject.put("v", request.getHeader("v"));
		return jsonObject;
	}
}
