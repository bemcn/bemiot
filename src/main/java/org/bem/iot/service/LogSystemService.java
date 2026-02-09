package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.tdengine.LogSystemMapper;
import org.bem.iot.model.log.LogSystem;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统日志服务
 * @author jakybland
 */
@Service
public class LogSystemService {
    @Resource
    LogSystemMapper logSystemMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取统计数量
     * @param example 查询条件
     * @return 返回数量值
     */
    public long count(QueryWrapper<LogSystem> example) {
        return logSystemMapper.selectCount(example);
    }

    /**
     * 查询列表
     * @param example 查询条件
     * @return 符合条件的数据列表
     */
    public List<LogSystem> select(QueryWrapper<LogSystem> example) {
        return logSystemMapper.selectList(example);
    }

    /**
     * 查询指定数量的数据
     * @param example 查询条件
     * @param size 显示数量
     * @return 符合条件的数据列表
     */
    public List<LogSystem> selectLimit(QueryWrapper<LogSystem> example, long size) {
        IPage<LogSystem> page = new Page<>(1L, size);
        IPage<LogSystem> result = logSystemMapper.selectPage(page, example);
        return result.getRecords();
    }

    /**
     * 分页查询数据
     * @param example 查询条件
     * @param index 查询的页码
     * @param size 每页数量
     * @return 返回查询结果
     */
    public IPage<LogSystem> selectPage(QueryWrapper<LogSystem> example, long index, long size) {
        Page<LogSystem> page = new Page<>(index, size);
        return logSystemMapper.selectPage(page, example);
    }

    /**
     * 插入新的数据
     * @param record 新数据
     */
    public void insert(LogSystem record) {
        logSystemMapper.insert(record);
    }

    /**
     * 插入新的数据
     * @param accessToken 用户Access Token
     * @param modelName 模块名称
     * @param operation 操作类型
     * @param description 操作描述
     * @throws Exception 异常信息
     */
    public void insert(String accessToken, String modelName, String operation, String description) throws Exception {
        try {
            //获取用户ID
            String key = "accToken:" + accessToken;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                String tokenValue = stringRedisTemplate.opsForValue().get(key);
                JSONObject tokenData = JSONObject.parseObject(tokenValue);
                if (tokenData != null) {
                    int userId = tokenData.getIntValue("userId");
                    String userName = tokenData.getString("userName");
                    String nickName = tokenData.getString("nickName");
                    String clientSource = tokenData.getString("clientSource");
                    String ipAddress = tokenData.getString("ipAddress");

                    String id = InternalIdUtil.createId();

                    LogSystem log = new LogSystem();
                    log.setTs(System.currentTimeMillis());
                    log.setLogId(id);
                    log.setClientSource(clientSource);
                    log.setClientIp(ipAddress);
                    log.setUserId(userId);
                    log.setUserName(userName);
                    log.setNickName(nickName);
                    log.setModelName(modelName);
                    log.setOperation(operation);
                    log.setDescription(description);
                    logSystemMapper.insert(log);
                }
            }
        } catch (Exception e) {
            throw new Exception("添加日志失败:",  e);
        }
    }

    /**
     * 删除数据
     * @param id 删除的ID
     */
    public void del(@Param("id") String id) {
        QueryWrapper<LogSystem> example = new QueryWrapper<>();
        example.eq("id", id);
        logSystemMapper.delete(example);
    }

    /**
     * 批量删除数据
     * @param idList 删除的ID列表
     */
    public void delArray(List<String> idList) {
        QueryWrapper<LogSystem> example = new QueryWrapper<>();
        example.in("id", idList);
        logSystemMapper.delete(example);
    }
}
