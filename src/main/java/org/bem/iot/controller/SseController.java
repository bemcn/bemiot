package org.bem.iot.controller;

import org.bem.iot.util.SseEmitterUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE控制器
 * @author jaky
 */
@RestController
@RequestMapping("/sse")
public class SseController {
    /**
     * 获取实时服务器信息
     * @param clientId 客户端ID
     */
    @GetMapping(value = "/serverMonitor", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter serverMonitor(@RequestParam(name="clientId", defaultValue = "") String clientId) {
        return SseEmitterUtil.create(clientId, "serverMonitor");
    }
}
