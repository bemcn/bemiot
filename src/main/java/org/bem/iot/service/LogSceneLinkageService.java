package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SceneGroupMapper;
import org.bem.iot.mapper.postgresql.SceneLinkageMapper;
import org.bem.iot.mapper.tdengine.LogSceneLinkageMapper;
import org.bem.iot.model.log.LogSceneLinkage;
import org.bem.iot.model.scene.SceneGroup;
import org.bem.iot.model.scene.SceneLinkage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 场景互动日志
 * @author jakybland
 */
@Service
public class LogSceneLinkageService {
    @Resource
    LogSceneLinkageMapper logSceneLinkageMapper;

    @Resource
    SceneLinkageMapper sceneLinkageMapper;

    @Resource
    SceneGroupMapper sceneGroupMapper;

    /**
     * 获取统计数量
     * @param example 查询条件
     * @return 返回数量值
     */
    public long count(QueryWrapper<LogSceneLinkage> example) {
        return logSceneLinkageMapper.selectCount(example);
    }

    /**
     * 查询指定数量的数据
     * @param example 查询条件
     * @param size 显示数量
     * @return 符合条件的数据列表
     */
    public List<LogSceneLinkage> selectLimit(QueryWrapper<LogSceneLinkage> example, long size) {
        IPage<LogSceneLinkage> page = new Page<>(1L, size);
        IPage<LogSceneLinkage> result = logSceneLinkageMapper.selectPage(page, example);
        List<LogSceneLinkage> list = result.getRecords();
        for (LogSceneLinkage log : list) {
            long sceneId = log.getSceneId();
            int sceneGroupId = log.getSceneGroupId();

            SceneLinkage scene = sceneLinkageMapper.selectById(sceneId);
            SceneGroup group = sceneGroupMapper.selectById(sceneGroupId);

            log.setScene(scene);
            log.setGroup(group);
        }
        return list;
    }

    /**
     * 分页查询数据
     * @param example 查询条件
     * @param index 查询的页码
     * @param size 每页数量
     * @return 返回查询结果
     */
    public IPage<LogSceneLinkage> selectPage(QueryWrapper<LogSceneLinkage> example, long index, long size) {
        Page<LogSceneLinkage> page = new Page<>(index, size);
        IPage<LogSceneLinkage> result = logSceneLinkageMapper.selectPage(page, example);
        List<LogSceneLinkage> list = result.getRecords();
        for (LogSceneLinkage log : list) {
            long sceneId = log.getSceneId();
            int sceneGroupId = log.getSceneGroupId();

            SceneLinkage scene = sceneLinkageMapper.selectById(sceneId);
            SceneGroup group = sceneGroupMapper.selectById(sceneGroupId);

            log.setScene(scene);
            log.setGroup(group);
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 插入新的数据
     * @param record 新数据
     */
    public void insert(LogSceneLinkage record) {
        logSceneLinkageMapper.insert(record);
    }

    /**
     * 删除数据
     * @param id 删除的ID
     */
    public void del(@Param("id") String id) {
        QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
        example.eq("log_id", id);
        logSceneLinkageMapper.delete(example);
    }

    /**
     * 批量删除数据
     * @param idList 删除的ID列表
     */
    public void delArray(List<String> idList) {
        QueryWrapper<LogSceneLinkage> example = new QueryWrapper<>();
        example.in("log_id", idList);
        logSceneLinkageMapper.delete(example);
    }
}
