package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.entity.ProductClassImp;
import org.bem.iot.mapper.postgresql.ProductClassMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductClass;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品分类
 * @author jakybland
 */
@Service
public class ProductClassService {
    @Resource
    ProductClassMapper productClassMapper;

    @Resource
    ProductMapper productMapper;

    /**
     * 查询分类列表
     * @return 返回分类列表
     */
    public List<ProductClass> select() {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.orderByAsc("order_num");
        List<ProductClass> classList = productClassMapper.selectList(example);
        List<ProductClass> list = new ArrayList<>();
        for (ProductClass item : classList) {
            list.add(item);
            int classId = item.getClassId();

            QueryWrapper<ProductClass> subExample = new QueryWrapper<>();
            subExample.eq("level_id", classId);
            subExample.orderByAsc("order_num");
            List<ProductClass> subList = productClassMapper.selectList(example);
            list.addAll(subList);
        }
        return list;
    }

    /**
     * 获取分类树型表
     * @return 分类列表
     */
    public JSONArray selectTreeTable() {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.orderByAsc("order_num");
        List<ProductClass> classList = productClassMapper.selectList(example);
        JSONArray list = new JSONArray();
        for (ProductClass item : classList) {
            int classId = item.getClassId();
            long productCount = 0L;

            QueryWrapper<ProductClass> subExample = new QueryWrapper<>();
            subExample.eq("level_id", classId);
            subExample.orderByAsc("order_num");
            List<ProductClass> subList = productClassMapper.selectList(subExample);
            JSONArray subArray = new JSONArray();
            for (ProductClass subItem : subList) {
                int subClassId = subItem.getClassId();
                long subProductCount = productMapper.selectCount(new QueryWrapper<Product>().eq("class_id", subClassId));
                productCount += subProductCount;

                JSONObject subObj = createObjectData(subItem, subProductCount);
                subArray.add(subObj);
            }

            JSONObject obj = createObjectData(item, productCount);
            obj.put("children", subArray);
            list.add(obj);
        }
        return list;
    }
    private JSONObject createObjectData(ProductClass item, long count) {
        JSONObject obj = new JSONObject();
        obj.put("classId", item.getClassId());
        obj.put("className", item.getClassName());
        obj.put("levelId", item.getLevelId());
        obj.put("levelName", item.getLevelName());
        obj.put("orderNum", item.getOrderNum());
        obj.put("remark", item.getRemark());
        obj.put("productCount", count);
        return obj;
    }


    /**
     * 获取分类树
     * @return 分类树
     */
    public JSONArray selectTree() {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        List<ProductClass> classList = productClassMapper.selectList(example);
        JSONArray list = new JSONArray();
        for (ProductClass item : classList) {
            int classId = item.getClassId();

            QueryWrapper<ProductClass> subExample = new QueryWrapper<>();
            subExample.eq("level_id", classId);
            subExample.orderByAsc("order_num");
            List<ProductClass> subList = productClassMapper.selectList(subExample);
            if(!subList.isEmpty()) {
                JSONArray subArray = new JSONArray();
                for (ProductClass subItem : subList) {
                    JSONObject subObj = createObjectItem(subItem);
                    subObj.put("hasChildren", false);
                    subArray.add(subObj);
                }

                JSONObject obj = createObjectItem(item);
                obj.put("hasChildren", true);
                obj.put("children", subArray);
                list.add(obj);
            } else {
                JSONObject obj = createObjectItem(item);
                obj.put("hasChildren", false);
            }
        }
        return list;
    }
    private JSONObject createObjectItem(ProductClass item) {
        JSONObject obj = new JSONObject();
        obj.put("key", item.getClassId() + "");
        obj.put("label", item.getClassName());
        obj.put("value", item.getClassId() + "");
        obj.put("levelId", item.getLevelId());
        return obj;
    }

    /**
     * 判断分类ID是否存在
     * @param classId 分类ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotClassId(int classId) {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("class_id", classId);
        return !productClassMapper.exists(example);
    }

    /**
     * 判断分类名称是否存在
     * @param levelId 上级分类ID
     * @param className 分类名称
     * @return 存在返回false，不存在返回true
     */
    public boolean existsClassName(int levelId, String className) {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", levelId);
        example.eq("class_name", className);
        return productClassMapper.exists(example);
    }

    /**
     * 判断分类名称是否存在
     * @param classId 分类ID
     * @param levelId 上级分类ID
     * @param className 分类名称
     * @return 存在返回false，不存在返回true
     */
    public boolean existsClassName(int classId, int levelId, String className) {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.ne("class_id", classId);
        example.eq("level_id", levelId);
        example.eq("class_name", className);
        return productClassMapper.exists(example);
    }

    /**
     * 查询分类
     * @param classId 分类ID
     * @return 分类信息
     */
    public ProductClass find(@Param("classId") int classId) {
        return productClassMapper.selectById(classId);
    }

    /**
     * 添加分类
     * @param record 分类信息
     * @throws Exception 异常信息
     */
    public void insert(ProductClass record) throws Exception {
        record.setClassId(null);
        int levelId = record.getLevelId();
        String levelName = "";
        if(levelId > 0) {
            ProductClass levelClass = productClassMapper.selectById(levelId);
            levelName = levelClass.getClassName();
        }
        int orderNum = productClassMapper.selectMax(levelId) + 1;

        record.setLevelName(levelName);
        record.setOrderNum(orderNum);
        int count = productClassMapper.insert(record);
        if(count < 1) {
            throw new Exception("新增产品分类失败");
        }
    }

    /**
     * 添加分类
     * @param classImp 分类信息
     * @return 返回结果 true:成功，false:失败
     */
    public boolean insertImport(ProductClassImp classImp) {
        try {
            if(StrUtil.isEmpty(classImp.getClassName())) {
                return false;
            } else {
                String className = classImp.getClassName();
                String levelName = classImp.getLevelName() == null ? "" : classImp.getLevelName();
                boolean hasLevel = false;
                int levelId = 0;
                if(levelName.isEmpty()) {
                    hasLevel = true;
                } else {
                    int itemId = queryLevelId(levelName);
                    if(itemId > 0) {
                        levelId = itemId;
                        hasLevel = true;
                    }
                }

                if(hasLevel) {
                    int orderNum = productClassMapper.selectMax(levelId) + 1;

                    ProductClass record = new ProductClass();
                    record.setClassId(null);
                    record.setClassName(className);
                    record.setLevelId(levelId);
                    record.setLevelName(levelName);
                    record.setOrderNum(orderNum);
                    record.setRemark(classImp.getRemark());
                    int ret = productClassMapper.insert(record);
                    return ret > 0;
                } else {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
    }
    private int queryLevelId(String className) {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", 0);
        example.eq("class_name", className);
        ProductClass data = productClassMapper.selectOne(example);
        return data == null ? 0 : data.getClassId();
    }

    /**
     * 修改分类
     * @param record 分类信息
     */
    public void update(ProductClass record) {
        productClassMapper.updateById(record);
    }

    /**
     * 修改排序
     * @param classId 分类ID
     * @param orderNumber 排序值
     */
    public void updateOrder(int classId, int orderNumber) {
        ProductClass record = productClassMapper.selectById(classId);
        record.setOrderNum(orderNumber);
        productClassMapper.updateById(record);
    }

    /**
     * 删除分类
     * @param classId 分类ID
     * @return 删除数量
     */
    public int del(int classId) {
        int count = 0;
        ProductClass record = productClassMapper.selectById(classId);
        int levelId = record.getLevelId();
        if (levelId > 0) {
            if (existsNotProduct(classId)) {
                count = productClassMapper.deleteById(classId);
            }
        } else {
            if (existsNotProductByLevel(classId)) {
                productClassMapper.delete(new QueryWrapper<ProductClass>().eq("level_id", classId));
                count = productClassMapper.deleteById(classId);
            }
        }
        return count;
    }
    private boolean existsNotProduct(int classId) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.eq("class_id", classId);
        return productMapper.selectCount(example) == 0;
    }
    private boolean existsNotProductByLevel(int classId) {
        QueryWrapper<ProductClass> example = new QueryWrapper<>();
        example.eq("level_id", classId);
        List<ProductClass> subList = productClassMapper.selectList(example);
        if(!subList.isEmpty()) {
            List<Integer> idList = new ArrayList<>();
            for (ProductClass item : subList) {
                idList.add(item.getClassId());
            }

            QueryWrapper<Product> examplePro = new QueryWrapper<>();
            examplePro.in("class_id", idList);
            return productMapper.selectCount(examplePro) == 0;
        } else {
            return true;
        }
    }
}
