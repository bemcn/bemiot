package org.bem.iot.service;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存过期时间
 * @author jakybland
 */
@Service
public class CacheableTimerService {
    @Resource
    SystemConfigService systemConfigService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 手动设置缓存并指定过期时间
     * @param keyPrefix Key前缀
     * @param primaryKey 主键标识
     */
    public void setAppAuthCacheWithCustomExpire(String keyPrefix, Object primaryKey) {
        String key = keyPrefix + "::" + primaryKey.toString();
        String cacheExpire = systemConfigService.find("cacheExpire");
        int hours = Integer.parseInt(cacheExpire);
        redisTemplate.expire(key, hours, TimeUnit.HOURS);
    }
}
