package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SystemDictMapper;
import org.bem.iot.model.system.SystemDict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字典分类
 * @author jakybland
 */
@Service
public class SystemDictService {
    @Resource
    SystemDictMapper systemDictMapper;

    /**
     * 统计字典数量
     * @param typeId 字典类型ID
     * @return 字典数量
     */
    public long count(int typeId) {
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.eq("dict_type_id", typeId);
        return systemDictMapper.selectCount(example);
    }

    /**
     * 获取指定字典列表
     * @param typeId 字典类型ID
     * @return 字典列表
     */
    public List<SystemDict> select(int typeId) {
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.eq("dict_type_id", typeId);
        example.orderByAsc("order_num");
        return systemDictMapper.selectList(example);
    }

    /**
     * 分页查询字典列表
     * @param index 页码
     * @param size 每页数量
     * @return 字典列表
     */
    public IPage<SystemDict> selectPage(int typeId, long index, long size) {
        Page<SystemDict> page = new Page<>(index, size);
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.eq("dict_type_id", typeId);
        example.orderByAsc("order_num");
        return systemDictMapper.selectPage(page, example);
    }

    /**
     * 判断字典ID是否存在
     * @param dictId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotDictId(int dictId) {
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.eq("dict_id", dictId);
        return !systemDictMapper.exists(example);
    }

    /**
     * 查询字典
     * @param dictId 字典ID
     * @return 字典信息
     */
    public SystemDict find(@Param("dictId") int dictId) {
        return systemDictMapper.selectById(dictId);
    }

    /**
     * 添加字典
     * @param record 字典信息
     */
    public void insert(SystemDict record) {
        record.setDictId(null);
        systemDictMapper.insert(record);
    }

    /**
     * 修改字典
     * @param record 字典信息
     */
    public SystemDict update(@Param("record") SystemDict record) {
        systemDictMapper.updateById(record);
        return record;
    }

    /**
     * 修改排序值
     * @param dictId 字典ID
     * @param orderNum 排序值
     */
    public SystemDict updateOrder(@Param("dictId") int dictId, int orderNum) {
        SystemDict record = systemDictMapper.selectById(dictId);
        record.setOrderNum(orderNum);
        systemDictMapper.updateById(record);
        return record;
    }

    /**
     * 删除字典
     * @param dictId 字典ID
     */
    public int del(@Param("dictId") int dictId) {
        return systemDictMapper.deleteById(dictId);
    }

    /**
     * 批量删除字典
     * @param idList 字典ID列表
     */
    public int delArray(List<Integer> idList) {
        return systemDictMapper.deleteBatchIds(idList);
    }
}
