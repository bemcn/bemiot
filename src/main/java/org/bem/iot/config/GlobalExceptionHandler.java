package org.bem.iot.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常返回
 * @author jaky
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	@Resource
	HttpServletResponse response;

	@ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(Exception e) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		jsonObject.put("status", "fail");

        if (e instanceof NoHandlerFoundException) {
			jsonObject.put("code", 404);
			jsonObject.put("message", "无效的请求，接口不存在");
		} else if(e instanceof MethodArgumentNotValidException) {
			jsonObject.put("message", ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().get(0).getDefaultMessage());
		} else {
			jsonObject.put("code", 500);
			jsonObject.put("message", "请求异常：" + e.getMessage());
		}
		ResponseUtil.responseData(jsonObject, response);
    }
}
