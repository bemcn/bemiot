package org.bem.iot.controller;

import jakarta.annotation.Resource;
import org.bem.iot.service.MonitorsDriveRunService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 驱动运行监控 SSE
 */
@RestController
@RequestMapping("/monitorsDriveRun")
public class MonitorsDriveRunController {
    @Resource
    MonitorsDriveRunService monitorsDriveRunService;

    /**
     * 创建SSE连接
     * @return 返回SseEmitter对象
     */
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnect(@RequestParam(name="clientId", defaultValue = "") String clientId) {
        return monitorsDriveRunService.connect(clientId);
    }
}
