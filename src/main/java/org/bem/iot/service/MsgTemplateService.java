package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.MsgTemplateMapper;
import org.bem.iot.model.general.MsgTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息模板
 * @author jakybland
 */
@Service
public class MsgTemplateService {
    @Resource
    MsgTemplateMapper msgTemplateMapper;

    /**
     * 统计消息模板数量
     * @param example 统计条件
     * @return 消息模板数量
     */
    public long count(QueryWrapper<MsgTemplate> example) {
        return msgTemplateMapper.selectCount(example);
    }

    /**
     * 查询消息模板列表
     * @param example 查询条件
     * @return 消息模板列表
     */
    public List<MsgTemplate> select(QueryWrapper<MsgTemplate> example) {
        return msgTemplateMapper.selectList(example);
    }

    /**
     * 分页查询消息模板列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 消息模板列表
     */
    public IPage<MsgTemplate> selectPage(QueryWrapper<MsgTemplate> example, long index, long size) {
        Page<MsgTemplate> page = new Page<>(index, size);
        return msgTemplateMapper.selectPage(page, example);
    }

    /**
     * 判断消息模板ID是否存在
     * @param templateId 消息模板ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotTemplateId(long templateId) {
        QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
        example.eq("role_id", templateId);
        return !msgTemplateMapper.exists(example);
    }
    public boolean existsIdentity(String scopeApp, String identity) {
        QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
        example.eq("scope_app", scopeApp);
        example.eq("identity", identity);
        return msgTemplateMapper.exists(example);
    }
    public boolean existsIdentity(long templateId, String scopeApp, String identity) {
        QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
        example.ne("template_id", templateId);
        example.eq("scope_app", scopeApp);
        example.eq("identity", identity);
        return msgTemplateMapper.exists(example);
    }

    /**
     * 查询消息模板
     * @param templateId 消息模板ID
     * @return 消息模板信息
     */
    public MsgTemplate find(long templateId) {
        return msgTemplateMapper.selectById(templateId);
    }

    /**
     * 查询消息模板
     * @param identity 模板标识
     * @param scopeApp 运用范围
     * @return 消息模板信息
     */
    public MsgTemplate findIdentity(String identity, String scopeApp) {
        QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
        example.eq("scope_app", scopeApp);
        example.eq("identity", identity);
        return msgTemplateMapper.selectOne(example);
    }

    /**
     * 添加消息模板
     * @param record 消息模板信息
     */
    public void insert(MsgTemplate record) {
        record.setTemplateId(null);
        record.setCreateTime(new Date());
        msgTemplateMapper.insert(record);
    }

    /**
     * 修改消息模板
     * @param record 消息模板信息
     */
    public void update(MsgTemplate record) {
        msgTemplateMapper.updateById(record);
    }

    /**
     * 删除消息模板 (删除前需验证是否存在用户)
     * @param templateId 消息模板ID
     * @return 删除数量
     */
    public int del(long templateId) {
        return msgTemplateMapper.deleteById(templateId);
    }

    /**
     * 批量删除消息模板 (删除前需验证是否存在用户)
     * @param idList 消息模板ID列表
     * @return 删除数量
     */
    public int delArray(List<Long> idList) {
        return msgTemplateMapper.deleteBatchIds(idList);
    }
}
