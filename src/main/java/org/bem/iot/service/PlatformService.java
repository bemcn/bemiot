package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.PlatformMapper;
import org.bem.iot.model.general.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 第3方平台接入
 * @author jakybland
 */
@Service
public class PlatformService {
    @Resource
    PlatformMapper platformMapper;

    /**
     * 统计第3方平台接入数量
     * @param example 统计条件
     * @return 第3方平台接入数量
     */
    public long count(QueryWrapper<Platform> example) {
        return platformMapper.selectCount(example);
    }

    /**
     * 获取指定第3方平台接入列表
     * @param example 查询条件
     * @return 第3方平台接入列表
     */
    public List<Platform> select(QueryWrapper<Platform> example) {
        return platformMapper.selectList(example);
    }

    /**
     * 分页查询第3方平台接入列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 第3方平台接入列表
     */
    public IPage<Platform> selectPage(QueryWrapper<Platform> example, long index, long size) {
        Page<Platform> page = new Page<>(index, size);
        return platformMapper.selectPage(page, example);
    }

    /**
     * 判断第3方平台接入ID是否存在
     * @param platformId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotPlatformId(String platformId) {
        QueryWrapper<Platform> example = new QueryWrapper<>();
        example.eq("platform_id", platformId);
        return !platformMapper.exists(example);
    }

    /**
     * 查询第3方平台接入
     * @param platformId 第3方平台接入ID
     * @return 第3方平台接入信息
     */
    public Platform find(String platformId) {
        if (platformId == null) {
            throw new IllegalArgumentException("platformId cannot be null");
        }
        return platformMapper.selectById(platformId);
    }

    /**
     * 添加第3方平台接入
     * @param record 第3方平台接入信息
     */
    public void insert(Platform record) {
        platformMapper.insert(record);
    }

    /**
     * 修改第3方平台接入
     * @param record 第3方平台接入信息
     */
    public void update(@Param("record") Platform record) {
        platformMapper.updateById(record);
    }

    /**
     * 修改状态
     * @param platformId 第3方平台接入ID
     */
    public void updateStatus(String platformId) {
        Platform record = platformMapper.selectById(platformId);
        int status = record.getStatus();
        if(status == 0) {
            record.setStatus(1);
        } else {
            record.setStatus(0);
        }
        platformMapper.updateById(record);
    }

    /**
     * 删除第3方平台接入
     * @param platformId 第3方平台接入ID
     */
    public int del(@Param("platformId") String platformId) {
        return platformMapper.deleteById(platformId);
    }

    /**
     * 批量删除第3方平台接入
     * @param idList 第3方平台接入ID列表
     */
    public int delArray(List<String> idList) {
        return platformMapper.deleteBatchIds(idList);
    }
}
