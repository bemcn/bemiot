package org.bem.iot.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.service.MonitorsCacheService;
import org.bem.iot.util.ResponseUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 缓存监控 SSE
 */
@RestController
@RequestMapping("/monitorsCache")
public class MonitorsCacheController {
    @Resource
    MonitorsCacheService monitorsCacheService;

    @Resource
    HttpServletResponse response;

    /**
     * 创建SSE连接
     * @return 返回SseEmitter对象
     */
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnect(@RequestParam(name="clientId", defaultValue = "") String clientId) {
        return monitorsCacheService.connect(clientId);
    }

    /**
     * 获取缓存信息
     */
    @GetMapping("/getCacheInfo")
    @PublicHeadLimit
    @ResponseBody
    public void getCacheInfo() {
        JSONObject obj = monitorsCacheService.getCacheInfo();
        JSONObject jsonObject = ResponseUtil.getSuccessJson(obj);
        ResponseUtil.responseData(jsonObject, response);
    }
}
