package org.bem.iot.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.mapper.postgresql.DriveMapper;
import org.bem.iot.model.general.Drive;
import org.bem.iot.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 驱动运行监控
 */
@Service
public class MonitorsDriveRunService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    DriveMapper driveMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 客户端连接 Key:客户端id  Value:SseEmitter
     */
    private static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter connect(String clientId) {
        SseEmitter sseEmitter = new SseEmitter();
        JSONObject sseMessage;
        if (StrUtil.isEmpty(clientId)) {
            return SseExceptionHandle(sseEmitter, "clientId不能为空");
        }
        if (sseEmitterMap.containsKey(clientId)) {
            return SseExceptionHandle(sseEmitter, "该clientId已绑定指定的客户端！clientId:" + clientId);
        }

        if (sseEmitterMap.size() > 1000) {
            return SseExceptionHandle(sseEmitter, "客户端连接过多，请稍后重试！");
        }

        // 连接成功需要返回数据，否则会出现待处理状态
        try {
            sseMessage = ResponseUtil.getSuccessJson(HttpServletResponse.SC_OK, "连接成功！");
            sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            logger.error("sse连接发送数据时出现异常：", e);
            throw new RuntimeException("sse连接发送数据时出现异常！");
        }

        // 连接断开
        sseEmitter.onCompletion(() -> {
            logger.info("sse连接断开，clientId为：{}", clientId);
            sseEmitterMap.remove(clientId);
        });

        // 连接超时
        sseEmitter.onTimeout(() -> {
            logger.info("sse连接已超时，clientId为：{}", clientId);
            sseEmitterMap.remove(clientId);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            logger.info("sse连接异常:", throwable);
            sseEmitterMap.remove(clientId);
        });
        sseEmitterMap.put(clientId, sseEmitter);

        return sseEmitter;
    }

    public boolean sendMessage() {
        if (ObjectUtil.isEmpty(sseEmitterMap)) {
            return false;
        }

        List<Drive> driveList = driveMapper.selectList(null);
        JSONArray dataArray = new JSONArray();
        for (Drive drive : driveList) {
            String driveCode = drive.getDriveCode();
            String driveName = drive.getDriveName();
            int sendNumber = 0;
            int receiveNumber = 0;

            String key = "drive:statistics:" + driveCode;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                String value = stringRedisTemplate.opsForValue().get(key);
                try {
                    JSONObject obj = JSONObject.parseObject(value);
                    if (obj != null) {
                        sendNumber = obj.getIntValue("sendNumber");
                        receiveNumber = obj.getIntValue("receiveNumber");
                    }
                } catch (Exception ignored) {
                }
            }

            JSONObject item = new JSONObject();
            item.put("driveCode", driveCode);
            item.put("driveName", driveName);
            item.put("sendNumber", sendNumber);
            item.put("receiveNumber", receiveNumber);
            dataArray.add(item);
        }

        for (Map.Entry<String, SseEmitter> entry : sseEmitterMap.entrySet()) {
            SseEmitter sseEmitter = entry.getValue();
            JSONObject sseMessage = ResponseUtil.getSuccessJson(dataArray);
            try {
                sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                logger.error("sse发送数据异常：", e);
            }
        }
        return true;
    }

    private static SseEmitter SseExceptionHandle(SseEmitter sseEmitter, String exceptionMessage) {
        try {
            JSONObject sseMessage = ResponseUtil.getErrorJson(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exceptionMessage);
            sseEmitter.send(sseMessage, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            throw new RuntimeException("sse发送数据异常！");
        }
        return sseEmitter;
    }
}
