package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.ProtocolsMapper;
import org.bem.iot.model.general.Protocols;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通讯协议
 * @author JiangShiYi
 */
@Service
public class ProtocolsService {
    @Resource
    ProtocolsMapper protocolsMapper;

    /**
     * 统计协议数量
     * @param example 统计条件
     * @return 协议数量
     */
    public long count(QueryWrapper<Protocols> example) {
        return protocolsMapper.selectCount(example);
    }

    /**
     * 查询协议列表
     * @param example 查询条件
     * @return 协议列表
     */
    public List<Protocols> select(QueryWrapper<Protocols> example) {
        return protocolsMapper.selectList(example);
    }

    /**
     * 分页查询协议列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 协议列表
     */
    public IPage<Protocols> selectPage(QueryWrapper<Protocols> example, long index, long size) {
        Page<Protocols> page = new Page<>(index, size);
        return protocolsMapper.selectPage(page, example);
    }

    /**
     * 判断协议ID是否存在
     * @param protocolId 协议ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotProtocolsId(int protocolId) {
        QueryWrapper<Protocols> example = new QueryWrapper<>();
        example.eq("protocol_id", protocolId);
        return !protocolsMapper.exists(example);
    }

    /**
     * 查询协议
     * @param protocolId 协议ID
     * @return 协议信息
     */
    public Protocols find(@Param("protocolId") int protocolId) {
        return protocolsMapper.selectById(protocolId);
    }

    /**
     * 添加协议
     * @param record 协议信息
     * @throws Exception 异常信息
     */
    public void insert(Protocols record) throws Exception {
        record.setProtocolId(null);
        protocolsMapper.insert(record);
    }

    /**
     * 修改协议
     * @param record 协议信息
     */
    public void update(@Param("record") Protocols record) {
        protocolsMapper.updateById(record);
    }

    /**
     * 删除协议 (删除前需验证是否存在设备)
     * @param protocolId 协议ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("protocolId") int protocolId) throws Exception {
        QueryWrapper<Protocols> example = new QueryWrapper<>();
        example.eq("protocol_id", protocolId);
        example.eq("built", 0);
        return protocolsMapper.delete(example);
    }

    /**
     * 批量删除协议 (删除前需验证是否存在设备)
     * @param idList 协议ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<Integer> idList) throws Exception {
        QueryWrapper<Protocols> example = new QueryWrapper<>();
        example.in("protocol_id", idList);
        example.eq("built", 0);
        return protocolsMapper.delete(example);
    }
}
