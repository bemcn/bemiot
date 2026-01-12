package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SystemDictMapper;
import org.bem.iot.mapper.postgresql.SystemDictTypeMapper;
import org.bem.iot.model.system.SystemDict;
import org.bem.iot.model.system.SystemDictType;
import org.bem.iot.model.system.SystemDictTypeVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字典分类
 * @author jakybland
 */
@Service
public class SystemDictTypeService {
    @Resource
    SystemDictTypeMapper systemDictTypeMapper;

    @Resource
    SystemDictMapper systemDictMapper;

    /**
     * 统计字典类型数量
     * @param example 查询条件
     * @return 字典类型数量
     */
    public long count(QueryWrapper<SystemDictType> example) {
        return systemDictTypeMapper.selectCount(example);
    }

    /**
     * 查询字典类型列表
     * @param example 查询条件
     * @return 字典类型列表
     */
    public List<SystemDictTypeVo> select(QueryWrapper<SystemDictType> example) {
        return systemDictTypeMapper.selectByCopies(example);
    }

    /**
     * 分页查询字典类型列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 字典类型列表
     */
    public IPage<SystemDictTypeVo> selectPage(QueryWrapper<SystemDictType> example, long index, long size) {
        Page<SystemDictType> page = new Page<>(index, size);
        return systemDictTypeMapper.selectPageByCopies(page, example);
    }

    /**
     * 判断ID是否存在
     * @param typeId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotTypeId(int typeId) {
        QueryWrapper<SystemDictType> example = new QueryWrapper<>();
        example.eq("dict_type_id", typeId);
        return !systemDictTypeMapper.exists(example);
    }

    /**
     * 查询字典类型
     * @param typeId 字典类型ID
     * @return 字典类型信息
     */
    public SystemDictType find(@Param("typeId") int typeId) {
        return systemDictTypeMapper.selectById(typeId);
    }

    /**
     * 添加字典类型
     * @param record 字典类型信息
     * @throws Exception 异常信息
     */
    public void insert(SystemDictType record) throws Exception {
        record.setDictTypeId(null);
        int ret = systemDictTypeMapper.insert(record);
        if(ret < 1) {
            throw new Exception("新增字典类型失败");
        }
    }

    /**
     * 修改字典类型
     * @param record 字典类型信息
     */
    public SystemDictType update(@Param("record") SystemDictType record) {
        systemDictTypeMapper.updateById(record);
        return record;
    }

    /**
     * 删除字典类型
     * @param typeId 字典类型ID
     */
    public int del(@Param("typeId") int typeId) {
        deleteByDict(typeId);
        return systemDictTypeMapper.deleteById(typeId);
    }

    /**
     * 批量删除字典类型
     * @param idList 字典类型ID列表
     */
    public int delArray(List<Integer> idList) {
        deleteArrayByDict(idList);
        return systemDictTypeMapper.deleteBatchIds(idList);
    }

    private void deleteByDict(int typeId) {
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.eq("dict_type_id", typeId);
        systemDictMapper.delete(example);
    }

    private void deleteArrayByDict(List<Integer> typeIdList) {
        QueryWrapper<SystemDict> example = new QueryWrapper<>();
        example.in("dict_type_id", typeIdList);
        systemDictMapper.delete(example);
    }
}
