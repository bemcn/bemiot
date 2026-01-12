package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Properties;

@Service
public class MonitorsRedisService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis缓存统计
     * @return 返回统计数据
     */
    public JSONObject getRedisTotal() {
        // 获取Redis当前内存统计
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        Properties info = connection.serverCommands().info();
        long memoryStartup = 0L;
        long memoryDataset = 0L;
        long memorySystem = 0L;
        long inputTotal = 0L;
        long outputTotal = 0L;
        if (info != null) {
            // 总内存
            long memoryTotal = Long.parseLong(info.getProperty("used_memory"));
            // 启动内存
            memoryStartup = Long.parseLong(info.getProperty("used_memory_startup"));
            // 用户内存
            memoryDataset = Long.parseLong(info.getProperty("used_memory_dataset"));
            // 系统内存
            memorySystem = memoryTotal - memoryStartup - memoryDataset;
            // 网络入口流量字节数
            inputTotal = Long.parseLong(info.getProperty("total_net_input_bytes"));
            // 网络出口流量字节数
            outputTotal = Long.parseLong(info.getProperty("total_net_output_bytes"));
        } else {
            logger.error("Redis获取内存统计失败");
        }

        JSONObject memory = new JSONObject();
        memory.put("memoryStartup", memoryStartup);
        memory.put("memoryDataset", memoryDataset);
        memory.put("memorySystem", memorySystem);

        JSONObject net = new JSONObject();
        net.put("inputTotal", inputTotal);
        net.put("outputTotal", outputTotal);

        // 获取近今日24小时内存占比
        // 获取近今日24小时流量占比

        JSONObject data = new JSONObject();
        data.put("memoryPie", memory);
        data.put("netPie", net);
        return data;
    }
}
