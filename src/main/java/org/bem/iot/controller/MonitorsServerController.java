package org.bem.iot.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.MonitorsServerService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 服务器监控 SSE
 */
@RestController
@RequestMapping("/monitorsServer")
public class MonitorsServerController {
    @Resource
    MonitorsServerService monitorsServerService;

    @Resource
    HttpServletResponse response;

    /**
     * 创建SSE连接
     * @return 返回SseEmitter对象
     */
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnect(@RequestParam(name="clientId", defaultValue = "") String clientId) {
        return monitorsServerService.connect(clientId);
    }

    /**
     * 获取服务器信息
     */
    @GetMapping("/getServerInfo")
    @PublicHeadLimit
    @ResponseBody
    public void getServerInfo() {
        JSONObject obj = monitorsServerService.getServerOverview();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(obj);
        ResponseUtil.responseData(jsonObject, response);
    }
}
