package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.FirmwareVersionMapper;
import org.bem.iot.model.general.FirmwareVersion;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 固件版本
 * @author jakybland
 */
@Service
public class FirmwareVersionService {
    @Resource
    FirmwareVersionMapper firmwareVersionMapper;

    /**
     * 查询固件版本列表
     * @param example 查询条件
     * @return 固件列表
     */
    public List<FirmwareVersion> select(QueryWrapper<FirmwareVersion> example) {
        return firmwareVersionMapper.selectList(example);
    }

    /**
     * 分页查询固件版本列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 固件列表
     */
    public IPage<FirmwareVersion> selectPage(QueryWrapper<FirmwareVersion> example, long index, long size) {
        Page<FirmwareVersion> page = new Page<>(index, size);
        return firmwareVersionMapper.selectPage(page, example);
    }

    /**
     * 判断固件版本ID是否存在
     * @param versionId 版本ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotVersionId(long versionId) {
        QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
        example.eq("version_id", versionId);
        return !firmwareVersionMapper.exists(example);
    }

    /**
     * 查询固件版本
     * @param versionId 版本ID
     * @return 固件信息
     */
    public FirmwareVersion find(@Param("versionId") long versionId) {
        return firmwareVersionMapper.selectById(versionId);
    }

    /**
     * 删除固件版本
     * @param versionId 版本ID
     */
    public int del(@Param("versionId") long versionId) {
        return firmwareVersionMapper.deleteById(versionId);
    }
}
