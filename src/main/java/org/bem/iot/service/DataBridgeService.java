package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DataBridgeMapper;
import org.bem.iot.model.scene.DataBridge;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据桥接
 * @author your-name
 */
@Service
public class DataBridgeService {
    @Resource
    DataBridgeMapper dataBridgeMapper;

    /**
     * 统计数据桥接数量
     * @param example 统计条件
     * @return 数据桥接数量
     */
    public long count(QueryWrapper<DataBridge> example) {
        return dataBridgeMapper.selectCount(example);
    }

    /**
     * 查询数据桥接列表
     * @return 数据桥接列表
     */
    public List<DataBridge> select(QueryWrapper<DataBridge> example) {
        return dataBridgeMapper.selectList(example);
    }

    /**
     * 分页查询数据桥接列表
     * @param index 页码
     * @param size 每页数量
     * @return 数据桥接列表
     */
    public IPage<DataBridge> selectPage(QueryWrapper<DataBridge> example, long index, long size) {
        Page<DataBridge> page = new Page<>(index, size);
        return dataBridgeMapper.selectPage(page, example);
    }

    /**
     * 判断数据桥接ID是否存在
     * @param bridgeId 数据桥接ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotBridgeId(String bridgeId) {
        QueryWrapper<DataBridge> example = new QueryWrapper<>();
        example.eq("bridge_id", bridgeId);
        return !dataBridgeMapper.exists(example);
    }

    /**
     * 查询数据桥接
     * @param bridgeId 数据桥接ID
     * @return 数据桥接信息
     */
    @Cacheable(value = "data_bridge", key = "#p0")
    public DataBridge find(@Param("bridgeId") String bridgeId) {
        return dataBridgeMapper.selectById(bridgeId);
    }

    /**
     * 添加数据桥接
     * @param record 数据桥接信息
     */
    public void insert(DataBridge record) {
        String bridgeId = InternalIdUtil.createId();
        record.setBridgeId(bridgeId);
        dataBridgeMapper.insert(record);
    }

    /**
     * 修改数据桥接
     * @param record 数据桥接信息
     */
    @CachePut(value = "data_bridge", key = "#p0.bridgeId")
    public DataBridge update(@Param("record") DataBridge record) {
        dataBridgeMapper.updateById(record);
        return record;
    }

    /**
     * 修改状态
     * @param bridgeId 数据桥接ID
     * @param status 状态
     */
    @CachePut(value = "data_bridge", key = "#p0")
    public DataBridge updateStatus(@Param("bridgeId") String bridgeId, int status) {
        DataBridge record = dataBridgeMapper.selectById(bridgeId);
        record.setStatus(status);
        dataBridgeMapper.updateById(record);
        return record;
    }

    /**
     * 删除数据桥接
     * @param bridgeId 数据桥接ID
     * @return 删除数量
     */
    @CacheEvict(value = "data_bridge", key = "#p0")
    public int del(@Param("bridgeId") String bridgeId) {
        return dataBridgeMapper.deleteById(bridgeId);
    }

    /**
     * 批量删除数据桥接
     * @param idList 数据桥接ID列表
     * @return 删除数量
     */
    @CacheEvict(value = "data_bridge", allEntries = true)
    public int delArray(List<String> idList) {
        return dataBridgeMapper.deleteBatchIds(idList);
    }
}