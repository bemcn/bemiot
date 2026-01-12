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

import java.util.List;

/**
 * 联动分组
 * @author jakybland
 */
@Service
public class SceneGroupService {
    @Resource
    SceneGroupMapper sceneGroupMapper;

    @Resource
    SceneLinkageMapper sceneLinkageMapper;

    /**
     * 统计分组数量
     * @param example 统计条件
     * @return 分组数量
     */
    public long count(QueryWrapper<SceneGroup> example) {
        return sceneGroupMapper.selectCount(example);
    }

    /**
     * 查询分组列表
     * @return 分组列表
     */
    public List<SceneGroup> select() {
        QueryWrapper<SceneGroup> example = new QueryWrapper<>();
        example.orderByAsc("order_num");
        return sceneGroupMapper.selectList(example);
    }

    /**
     * 分页查询分组列表
     * @param index 页码
     * @param size 每页数量
     * @return 分组列表
     */
    public IPage<SceneGroup> selectPage(long index, long size) {
        QueryWrapper<SceneGroup> example = new QueryWrapper<>();
        example.orderByAsc("order_num");
        Page<SceneGroup> page = new Page<>(index, size);
        return sceneGroupMapper.selectPage(page, example);
    }

    /**
     * 判断分组ID是否存在
     * @param sceneGroupId 分组ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotSceneGroupId(int sceneGroupId) {
        QueryWrapper<SceneGroup> example = new QueryWrapper<>();
        example.eq("scene_group_id", sceneGroupId);
        return !sceneGroupMapper.exists(example);
    }

    /**
     * 查询分组
     * @param sceneGroupId 分组ID
     * @return 分组信息
     */
    public SceneGroup find(@Param("sceneGroupId") int sceneGroupId) {
        return sceneGroupMapper.selectById(sceneGroupId);
    }

    /**
     * 添加分组
     * @param record 分组信息
     * @throws Exception 异常信息
     */
    public void insert(SceneGroup record) throws Exception {
        int orderNum = sceneGroupMapper.selectMax() + 1;

        record.setSceneGroupId(null);
        record.setOrderNum(orderNum);
        int count = sceneGroupMapper.insert(record);
        if(count < 1) {
            throw new Exception("新增分组失败");
        }
    }

    /**
     * 修改分组
     * @param record 分组信息
     */
    public SceneGroup update(@Param("record") SceneGroup record) {
        sceneGroupMapper.updateById(record);
        return record;
    }

    /**
     * 修改排序
     * @param sceneGroupId 分组ID
     * @param orderNumber 排序值
     */
    public SceneGroup updateOrder(@Param("sceneGroupId") int sceneGroupId, int orderNumber) {
        SceneGroup record = sceneGroupMapper.selectById(sceneGroupId);
        record.setOrderNum(orderNumber);
        sceneGroupMapper.updateById(record);
        return record;
    }

    /**
     * 删除分组 (删除前需验证是否关联产品)
     * @param sceneGroupId 分组ID
     * @return 删除数量
     */
    public int del(@Param("sceneGroupId") int sceneGroupId) {
        delBySceneLinkage(sceneGroupId);
        return sceneGroupMapper.deleteById(sceneGroupId);
    }

    /**
     * 批量删除分组 (删除前需验证关联产品)
     * @param idList 分组ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        delArrayBySceneLinkage(idList);
        return sceneGroupMapper.deleteBatchIds(idList);
    }

    private void delBySceneLinkage(int sceneGroupId) {
        QueryWrapper<SceneLinkage> example = new QueryWrapper<>();
        example.eq("scene_group_id", sceneGroupId);
        sceneLinkageMapper.delete(example);
    }

    private void delArrayBySceneLinkage(List<Integer> idList) {
        QueryWrapper<SceneLinkage> example = new QueryWrapper<>();
        example.in("scene_group_id", idList);
        sceneLinkageMapper.delete(example);
    }
}
