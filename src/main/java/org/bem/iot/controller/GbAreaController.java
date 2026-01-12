package org.bem.iot.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.GbAreaCodeService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * gb28081 区域选择
 * @author jakybland
 */
@RestController
@RequestMapping("/gbArea")
public class GbAreaController {
    @Resource
    GbAreaCodeService gbAreaCodeService;

    @Resource
    HttpServletResponse response;

    /**
     * 获取属性结构
     */
    @GetMapping("/getGbAreaTree")
    @PublicHeadLimit
    @ResponseBody
    public void getGbAreaTree() {
        JSONObject jsonObject;
        try {
            JSONArray list = gbAreaCodeService.selectTree();

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
