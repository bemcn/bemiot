package org.bem.iot.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.bem.iot.global.CacheMonitorGlobal;
import org.bem.iot.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存监控
 */
@Service
public class MonitorsCacheService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    RedisTemplate<String, Object> redisTemplate;

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

        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        Properties info = connection.serverCommands().info();
        int connects = 0; // 客户端连接数

        BigDecimal usedSysCpu = BigDecimal.ZERO; // 主进程核心CPU消耗
        BigDecimal rSysCpu = BigDecimal.ZERO;
        BigDecimal usedUserCpu = BigDecimal.ZERO; // 主进程用户CPU消耗
        BigDecimal rUserCpu = BigDecimal.ZERO;
        BigDecimal usedMemory = BigDecimal.ZERO; // 已使用的内存
        BigDecimal inputBytes = BigDecimal.ZERO; // 网络入口流量
        BigDecimal rInputBytes = BigDecimal.ZERO;
        BigDecimal outputBytes = BigDecimal.ZERO; // 网络出口流量
        BigDecimal rOutputBytes = BigDecimal.ZERO;
        long keyHits = 0L; // 命中次数
        long rKeyHits = 0L;
        long keyMisses = 0L; // 没命中次数
        long rKeyMisses = 0L;
        long qpsSec = 0L; // 执行的命令数
        long rQpsSec = 0L;
        if (info != null) {
            connects = Integer.parseInt(info.getProperty("connected_clients"));

            BigDecimal usedMemoryInBytes = new BigDecimal(info.getProperty("used_memory"));
            BigDecimal bytesInMB = new BigDecimal(1024 * 1024);
            usedMemory = usedMemoryInBytes.divide(bytesInMB, 2, RoundingMode.HALF_UP);

            rSysCpu = new BigDecimal(info.getProperty("used_cpu_sys"));
            usedSysCpu = rSysCpu.subtract(CacheMonitorGlobal.lastUsedSysCpu);
            rUserCpu = new BigDecimal(info.getProperty("used_cpu_user"));
            usedUserCpu = rUserCpu.subtract(CacheMonitorGlobal.lastUsedUserCpu);

            rInputBytes = new BigDecimal(info.getProperty("total_net_input_bytes"));
            inputBytes = rInputBytes.subtract(CacheMonitorGlobal.lastInputBytes);
            rOutputBytes = new BigDecimal(info.getProperty("total_net_output_bytes"));
            outputBytes = rOutputBytes.subtract(CacheMonitorGlobal.lastOutputBytes);

            rKeyHits = Long.parseLong(info.getProperty("keyspace_hits"));
            keyHits = rKeyHits - CacheMonitorGlobal.lastKeyHits;
            rKeyMisses = Long.parseLong(info.getProperty("keyspace_misses"));
            keyMisses = rKeyMisses - CacheMonitorGlobal.lastKeyMisses;
            rQpsSec = Long.parseLong(info.getProperty("total_commands_processed"));
            qpsSec = rQpsSec - CacheMonitorGlobal.lastQpsSec;
        }
        CacheMonitorGlobal.lastUsedSysCpu = rSysCpu;
        CacheMonitorGlobal.lastUsedUserCpu = rUserCpu;
        CacheMonitorGlobal.lastInputBytes = rInputBytes;
        CacheMonitorGlobal.lastOutputBytes = rOutputBytes;
        CacheMonitorGlobal.lastKeyHits = rKeyHits;
        CacheMonitorGlobal.lastKeyMisses = rKeyMisses;
        CacheMonitorGlobal.lastQpsSec = rQpsSec;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("connects", connects);
        jsonObject.put("usedMemory", usedMemory);
        jsonObject.put("usedSysCpu", usedSysCpu);
        jsonObject.put("usedUserCpu", usedUserCpu);
        jsonObject.put("lastInputBytes", inputBytes);
        jsonObject.put("lastOutputBytes", outputBytes);
        jsonObject.put("keyHits", keyHits);
        jsonObject.put("keyMisses", keyMisses);
        jsonObject.put("qpsSec", qpsSec);

        for (Map.Entry<String, SseEmitter> entry : sseEmitterMap.entrySet()) {
            SseEmitter sseEmitter = entry.getValue();
            JSONObject sseMessage = ResponseUtil.getSuccessJson(jsonObject);
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

    /**
     * 获取缓存信息
     * @return 返回服务器概要信息
     */
    public JSONObject getCacheInfo() {
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        Properties info = connection.serverCommands().info("server");
        String version = "";
        String pid = "";
        String port = "";
        String runDays = "";
        String model = "";
        String archBits = "";
        if (info != null) {
            version = info.getProperty("redis_version");
            pid = info.getProperty("process_id");
            port = info.getProperty("tcp_port");
            runDays = info.getProperty("uptime_in_days");
            String modelType = info.getProperty("redis_mode");
            if("standalone".equals(modelType)) {
                model = "单机模式";
            } else if("cluster".equals(modelType)) {
                model = "集群模式";
            } else if("sentinel".equals(modelType)) {
                model = "哨兵模式";
            } else {
                model = "未知模式";
            }
            archBits = info.getProperty("arch_bits");
        }

        info = connection.serverCommands().info("memory");
        BigDecimal memory = BigDecimal.ZERO;
        if (info != null) {
            BigDecimal memoryBytes = new BigDecimal(info.getProperty("allocator_resident"));
            BigDecimal bytesInMB = new BigDecimal(1024 * 1024);
            memory = memoryBytes.divide(bytesInMB, 2, RoundingMode.HALF_UP);
        }

        JSONObject obj = new JSONObject();
        obj.put("version", version);
        obj.put("archBits", archBits);
        obj.put("pid", pid);
        obj.put("port", port);
        obj.put("runDays", runDays);
        obj.put("model", model);
        obj.put("memory", memory);
        return obj;
    }
}
