package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.bem.iot.entity.BaseFile;
import org.bem.iot.service.LoginService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.util.UploadFileUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传文件
 * @author jaky
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
	@Resource
	LoginService loginService;

	@Resource
	HttpServletRequest request;

	@Resource
	HttpServletResponse response;
	
	@PostMapping("/uploadFileBase")
	@ResponseBody
	public void uploadFileBase(@RequestBody String fileBase) {
		JSONObject jsonObject;
		OutputStream out = null;
		try {
			String accessToken = request.getHeader("accessToken");
			if (StrUtil.isEmpty(accessToken)) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else if (loginService.existsNotAccessToken(accessToken, "user")) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else {
				BaseFile baseFile = UploadFileUtil.createUploadConfig(fileBase);
				String fileData = baseFile.getFileData();
				String url = baseFile.getUrl();
				String saveDir = baseFile.getSaveDir();
				String saveUrl = baseFile.getSaveUrl();

				UploadFileUtil.verifyDir(saveDir);

				byte[] bytes = Base64.decodeBase64(fileData);
				for (int i = 0; i < bytes.length; ++i) {
					// 调整异常数据
					if (bytes[i] < 0) {
						bytes[i] += (byte) 256;
					}
				}

				out = new FileOutputStream(saveUrl);
				out.write(bytes);

				JSONObject data = new JSONObject();
				data.put("url", url);
				data.put("saveUrl", saveUrl);
				jsonObject = ResponseUtil.getSuccessJson(data);
			}
		} catch (Exception e) {
			jsonObject = ResponseUtil.getErrorJson(e.getMessage());
		} finally {
			try {
				if(out != null) {
					out.flush();
				}
			} catch (IOException ignored) {
			}
			try {
				if(out != null) {
					out.close();
				}
			} catch (IOException ignored) {
			}
		}
		ResponseUtil.responseData(jsonObject, response);
    }
	
	@PostMapping("/uploadFile")
	@ResponseBody
	public void uploadFile(@RequestParam(name="file") MultipartFile file) {
		JSONObject jsonObject;
		try {
			String accessToken = request.getHeader("accessToken");
			if (StrUtil.isEmpty(accessToken)) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else if (loginService.existsNotAccessToken(accessToken, "user")) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else {
				BaseFile baseFile = UploadFileUtil.createUploadConfig(file);
				String url = baseFile.getUrl();
				String saveDir = baseFile.getSaveDir();
				String saveName = baseFile.getSaveName();
				String saveUrl = baseFile.getSaveUrl();

				//检查目录是否存在
				UploadFileUtil.verifyDir(saveDir);

				//保存文件
				File saveFile = new File(saveDir, saveName);
				file.transferTo(saveFile);

				JSONObject data = new JSONObject();
				data.put("url", url);
				data.put("saveUrl", saveUrl);
				jsonObject = ResponseUtil.getSuccessJson(data);
			}
		} catch (Exception e) {
			jsonObject = ResponseUtil.getErrorJson(e.getMessage());
		}
		ResponseUtil.responseData(jsonObject, response);
    }

	@PostMapping("/uploadDrive")
	@ResponseBody
	public void uploadDrive(@RequestParam(name="file") MultipartFile file) {
		JSONObject jsonObject;
		try {
			String accessToken = request.getHeader("accessToken");
			if (StrUtil.isEmpty(accessToken)) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else if (loginService.existsNotAccessToken(accessToken, "user")) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else {
				BaseFile baseFile = UploadFileUtil.createUploadDrive(file);
				String url = baseFile.getUrl();
				String saveDir = baseFile.getSaveDir();
				String saveName = baseFile.getSaveName();
				String saveUrl = baseFile.getSaveUrl();

				//检查文件是否存在，如果存在则拒绝
				File fileFile = new File(saveUrl);
				if(fileFile.exists()) {
					jsonObject = ResponseUtil.getErrorJson("该文件已存在,请更改文件名（建议加版本号）");
				} else {
					//保存文件
					File saveFile = new File(saveDir, saveName);
					file.transferTo(saveFile);

					JSONObject data = new JSONObject();
					data.put("url", url);
					data.put("saveUrl", saveUrl);
					jsonObject = ResponseUtil.getSuccessJson(data);
				}
			}
		} catch (Exception e) {
			jsonObject = ResponseUtil.getErrorJson(e.getMessage());
		}
		ResponseUtil.responseData(jsonObject, response);
	}
	
	@PostMapping("/uploadFileArray")
	@ResponseBody
	public void uploadFileArray(@RequestParam("files") MultipartFile[] files) {
		JSONObject jsonObject;
		try {
			String accessToken = request.getHeader("accessToken");
			if (StrUtil.isEmpty(accessToken)) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else if (loginService.existsNotAccessToken(accessToken, "user")) {
				jsonObject = ResponseUtil.getErrorJson("未授权的上传请求");
			} else {
				List<BaseFile> baseList = UploadFileUtil.createUploadConfigList(files);
				List<Map<String, String>> list = new ArrayList<>();

				BaseFile firstFile = baseList.get(0);
				String saveDir = firstFile.getSaveDir();
				UploadFileUtil.verifyDir(saveDir);

				//保存文件
				for (int i = 0; i < baseList.size(); i++) {
					BaseFile baseFile = baseList.get(i);
					MultipartFile file = files[i];

					String url = baseFile.getUrl();
					String saveName = baseFile.getSaveName();
					String saveUrl = baseFile.getSaveUrl();

					File saveFile = new File(saveDir, saveName);
					file.transferTo(saveFile);

					Map<String, String> map = new HashMap<>();
					map.put("url", url);
					map.put("saveUrl", saveUrl);
					list.add(map);
				}

				jsonObject = ResponseUtil.getSuccessJson(list);
			}
		} catch (Exception e) {
			jsonObject = ResponseUtil.getErrorJson(e.getMessage());
		}
		ResponseUtil.responseData(jsonObject, response);
    }
}
