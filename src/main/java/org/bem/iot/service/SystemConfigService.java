package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SystemConfigMapper;
import org.bem.iot.model.system.SystemConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数
 * @author jakybland
 */
@Service
public class SystemConfigService {
    @Resource
    SystemConfigMapper systemConfigMapper;

    /**
     * 查询参数分组
     * @param group 分组标识
     * @return 所有参数字典
     */
    public Map<String, String> selectByGroup(@Param("group") String group) {
        QueryWrapper<SystemConfig> example = new QueryWrapper<>();
        example.eq("config_group", group);
        List<SystemConfig> list = systemConfigMapper.selectList(example);
        return configListToMap(list);
    }

    /**
     * 查询指定Key的值
     * @param key 指定Key
     * @return 指定Key的值
     */
    public String find(@Param("key") String key) {
        QueryWrapper<SystemConfig> example = new QueryWrapper<>();
        example.eq("config_key", key);
        if(systemConfigMapper.exists(example)) {
            SystemConfig item = systemConfigMapper.selectById(key);
            return item.getConfigValue();
        } else {
            throw new RuntimeException("参数不存在");
        }
    }

    /**
     * 查询多个指定Key的值
     * @param keys 指定Key数组
     * @return 所有参数字典
     */
    public Map<String, String> findArray(String[] keys) {
        List<String> keyList = Arrays.asList(keys);

        QueryWrapper<SystemConfig> example = new QueryWrapper<>();
        example.in("config_key", keyList);
        List<SystemConfig> list = systemConfigMapper.selectList(example);
        return configListToMap(list);
    }

    private Map<String, String> configListToMap(List<SystemConfig> list) {
        Map<String, String> map = new HashMap<>();
        for (SystemConfig item : list) {
            String key = item.getConfigKey();
            String value = item.getConfigValue();
            map.put(key, value);
        }
        return map;
    }

    /**
     * 批量修改数据 （Key/value）
     * @param record 分组数据
     */
    public void updateByGroup(JSONObject record) {
        for (String key : record.keySet()) {
            if(!"group".equals(key)) {
                SystemConfig item = new SystemConfig();
                item.setConfigKey(key);
                item.setConfigValue(record.getString(key));
                try {
                    systemConfigMapper.updateById(item);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
