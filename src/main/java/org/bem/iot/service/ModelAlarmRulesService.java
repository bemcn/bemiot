package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.ModelAlarmRulesMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.model.product.ModelAlarmRules;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物模型告警规则
 * @author jakybland
 */
@Service
public class ModelAlarmRulesService {
    @Resource
    ModelAlarmRulesMapper modelAlarmRulesMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductModelMapper productModelMapper;

    /**
     * 统计物模型规则数量
     * @param example 统计条件
     * @return 物模型规则数量
     */
    public long count(QueryWrapper<ModelAlarmRules> example) {
        return modelAlarmRulesMapper.selectCount(example);
    }

    /**
     * 获取指定物模型规则列表
     * @param example 查询条件
     * @return 物模型规则列表
     */
    public List<ModelAlarmRules> select(QueryWrapper<ModelAlarmRules> example) {
        List<ModelAlarmRules> list = modelAlarmRulesMapper.selectList(example);
        for (ModelAlarmRules item : list) {
            relevancyData(item);
        }
        return list;
    }

    /**
     * 分页查询物模型规则列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 物模型规则列表
     */
    public IPage<ModelAlarmRules> selectPage(QueryWrapper<ModelAlarmRules> example, long index, long size) {
        Page<ModelAlarmRules> page = new Page<>(index, size);
        IPage<ModelAlarmRules> pageData = modelAlarmRulesMapper.selectPage(page, example);
        List<ModelAlarmRules> list = pageData.getRecords();
        for (ModelAlarmRules item : list) {
            relevancyData(item);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断iD是否不存在
     * @param rulesId id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotModelAlarmRulesId(long rulesId) {
        QueryWrapper<ModelAlarmRules> example = new QueryWrapper<>();
        example.eq("rules_id", rulesId);
        return !modelAlarmRulesMapper.exists(example);
    }

    /**
     * 查询物模型规则
     * @param rulesId 物模型规则ID
     * @return 物模型规则信息
     */
    public ModelAlarmRules find(long rulesId) {
        ModelAlarmRules rules = modelAlarmRulesMapper.selectById(rulesId);
        relevancyData(rules);
        return rules;
    }

    private void relevancyData(ModelAlarmRules rules) {
        String productId = rules.getProductId();
        String modelIdentity = rules.getModelIdentity();

        Product product = productMapper.selectById(productId);

        QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
        exampleModel.eq("product_id", productId);
        exampleModel.eq("model_identity", modelIdentity);
        ProductModel model = productModelMapper.selectOne(exampleModel);

        rules.setProduct(product);
        rules.setModel(model);
    }

    /**
     * 添加物模型规则
     * @param record 物模型规则信息
     */
    public void insert(ModelAlarmRules record) {
        modelAlarmRulesMapper.insert(record);
    }

    /**
     * 修改物模型规则
     * @param record 物模型规则信息
     */
    public void update(ModelAlarmRules record) {
        modelAlarmRulesMapper.updateById(record);
    }

    /**
     * 删除物模型规则
     * @param rulesId 物模型规则ID
     */
    public int del(long rulesId) {
        return modelAlarmRulesMapper.deleteById(rulesId);
    }

    /**
     * 批量删除物模型规则
     * @param idList 物模型规则ID列表
     */
    @CacheEvict(value = "modelAlarmRules", allEntries = true)
    public int delArray(List<String> idList) {
        return modelAlarmRulesMapper.deleteBatchIds(idList);
    }
}
