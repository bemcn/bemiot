package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SceneGroupMapper;
import org.bem.iot.mapper.postgresql.SceneLinkageMapper;
import org.bem.iot.model.scene.SceneGroup;
import org.bem.iot.model.scene.SceneLinkage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 场景联动
 * @author jakybland
 */
@Service
public class SceneLinkageService {
    @Resource
    SceneLinkageMapper sceneLinkageMapper;
    
    @Resource
    SceneGroupMapper sceneGroupMapper;

    /**
     * 统计场景联动数量
     * @param example 统计条件
     * @return 场景联动数量
     */
    public long count(QueryWrapper<SceneLinkage> example) {
        return sceneLinkageMapper.selectCount(example);
    }

    /**
     * 查询场景联动列表
     * @return 场景联动列表
     */
    public List<SceneLinkage> select(QueryWrapper<SceneLinkage> example) {
        List<SceneLinkage> list = sceneLinkageMapper.selectList(example);
        for (SceneLinkage record : list) {
            associateGroup(record);
        }
        return list;
    }

    /**
     * 分页查询场景联动列表
     * @param index 页码
     * @param size 每页数量
     * @return 场景联动列表
     */
    public IPage<SceneLinkage> selectPage(QueryWrapper<SceneLinkage> example, long index, long size) {
        Page<SceneLinkage> page = new Page<>(index, size);
        IPage<SceneLinkage> pageData = sceneLinkageMapper.selectPage(page, example);
        List<SceneLinkage> list = pageData.getRecords();
        for (SceneLinkage record : list) {
            associateGroup(record);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断场景联动ID是否存在
     * @param sceneId 场景联动ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotSceneId(long sceneId) {
        QueryWrapper<SceneLinkage> example = new QueryWrapper<>();
        example.eq("scene_id", sceneId);
        return !sceneLinkageMapper.exists(example);
    }

    /**
     * 查询场景联动
     * @param sceneId 场景联动ID
     * @return 场景联动信息
     */
    public SceneLinkage find(@Param("sceneId") long sceneId) {
        SceneLinkage data = sceneLinkageMapper.selectById(sceneId);
        associateGroup(data);
        return data;
    }
    private void associateGroup(SceneLinkage data) {
        int groupId = data.getSceneGroupId();

        SceneGroup group = sceneGroupMapper.selectById(groupId);
        data.setGroup(group);
    }

    /**
     * 查询场景联动
     * @param sceneId 场景联动ID
     * @return 场景联动信息
     */
    public SceneLinkage findMeta(@Param("sceneId") long sceneId) {
        return sceneLinkageMapper.selectById(sceneId);
    }

    /**
     * 添加场景联动
     * @param record 场景联动信息
     */
    public void insert(SceneLinkage record) {
        record.setSceneId(null);
        record.setStatus(1);
        record.setCreateTime(new Date());
        sceneLinkageMapper.insert(record);
    }

    /**
     * 修改场景联动
     * @param record 场景联动信息
     */
    public SceneLinkage update(@Param("record") SceneLinkage record) {
        sceneLinkageMapper.updateById(record);
        associateGroup(record);
        return record;
    }

    /**
     * 修改状态
     * @param sceneId 场景联动ID
     * @param status 状态 0：停用 1：启用
     */
    public SceneLinkage updateStatus(@Param("sceneId") long sceneId, int status) {
        SceneLinkage record = sceneLinkageMapper.selectById(sceneId);
        record.setStatus(status);
        sceneLinkageMapper.updateById(record);
        associateGroup(record);
        return record;
    }

    /**
     * 删除场景联动 (删除前需验证是否关联产品)
     * @param sceneId 场景联动ID
     * @return 删除数量
     */
    public int del(@Param("sceneId") int sceneId) {
        return sceneLinkageMapper.deleteById(sceneId);
    }

    /**
     * 批量删除场景联动 (删除前需验证关联产品)
     * @param idList 场景联动ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        return sceneLinkageMapper.deleteBatchIds(idList);
    }
}
