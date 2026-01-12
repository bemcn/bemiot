package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.ProductAttrMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductAttr;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 档案属性
 * @author jakybland
 */
@Service
public class ProductAttrService {
    @Resource
    ProductAttrMapper productAttrMapper;

    @Resource
    ProductMapper productMapper;

    /**
     * 统计档案属性数量
     * @param example 统计条件
     * @return 档案属性数量
     */
    public long count(QueryWrapper<ProductAttr> example) {
        return productAttrMapper.selectCount(example);
    }

    /**
     * 查询档案属性列表
     * @param example 查询条件
     * @return 档案属性列表
     */
    public List<ProductAttr> select(QueryWrapper<ProductAttr> example) {
        List<ProductAttr> list = productAttrMapper.selectList(example);
        for(ProductAttr attr : list) {
            String productId = attr.getProductId();
            Product product = productMapper.selectById(productId);
            attr.setProduct(product);
        }
        return list;
    }

    /**
     * 分页查询档案属性列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 档案属性列表
     */
    public IPage<ProductAttr> selectPage(QueryWrapper<ProductAttr> example, long index, long size) {
        Page<ProductAttr> page = new Page<>(index, size);
        IPage<ProductAttr> pageData = productAttrMapper.selectPage(page, example);
        List<ProductAttr> list = pageData.getRecords();
        for(ProductAttr attr : list) {
            String productId = attr.getProductId();
            Product product = productMapper.selectById(productId);
            attr.setProduct(product);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断档案属性ID是否存在
     * @param attrId 档案属性ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotAttrId(long attrId) {
        QueryWrapper<ProductAttr> example = new QueryWrapper<>();
        example.eq("attr_id", attrId);
        return !productAttrMapper.exists(example);
    }

    /**
     * 查询档案属性
     * @param attrId 档案属性ID
     * @return 档案属性信息
     */
    public ProductAttr find(@Param("attrId") long attrId) {
        ProductAttr attr = productAttrMapper.selectById(attrId);
        String productId = attr.getProductId();
        Product product = productMapper.selectById(productId);
        attr.setProduct(product);
        return attr;
    }

    /**
     * 添加档案属性
     * @param record 档案属性信息
     * @return 添加结果
     */
    public int insert(ProductAttr record) {
        String productId = record.getProductId();
        Product product = productMapper.selectById(productId);
        String classRoute = product.getClassRoute();

        record.setAttrId(null);
        record.setClassRoute(classRoute);
        return productAttrMapper.insert(record);
    }

    /**
     * 修改档案属性
     * @param record 档案属性信息
     * @return 修改结果
     */
    public int update(ProductAttr record) {
        return productAttrMapper.updateById(record);
    }

    /**
     * 删除档案属性
     * @param attrId 档案属性ID
     * @return 删除结果
     */
    public int del(@Param("attrId") long attrId) {
        return productAttrMapper.deleteById(attrId);
    }

    /**
     * 批量删除档案属性
     * @param idList 档案属性ID列表
     * @return 删除结果
     */
    public int delArray(List<Long> idList) {
        return productAttrMapper.deleteBatchIds(idList);
    }
}