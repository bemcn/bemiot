package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.*;
import org.bem.iot.mapper.tdengine.IotStableMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 产品信息
 * @author jakybland
 */
@Service
public class ProductService {
    @Resource
    ProductMapper productMapper;

    @Resource
    ProductClassMapper productClassMapper;

    @Resource
    DriveMapper driveMapper;

    @Resource
    FirmwareMapper firmwareMapper;

    @Resource
    ProductAuthCodeMapper productAuthCodeMapper;

    @Resource
    ProductModelMapper productModeMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    IotStableMapper iotStableMapper;

    @Resource
    ModelAlarmRulesMapper modelAlarmRulesMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductModelService.class);


    /**
     * 统计产品数量
     * @param example 统计条件
     * @return 产品数量
     */
    public long count(QueryWrapper<Product> example) {
        return productMapper.selectCount(example);
    }

    /**
     * 查询产品列表
     * @param example 查询条件
     * @return 产品列表
     */
    public List<Product> select(QueryWrapper<Product> example) {
        return associateQueryByList(productMapper.selectList(example));
    }


    /**
     * 分页查询产品列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 产品列表
     */
    public IPage<Product> selectPage(QueryWrapper<Product> example, long index, long size) {
        Page<Product> page = new Page<>(index, size);
        IPage<Product> pageDb = productMapper.selectPage(page, example);
        List<Product> list = associateQueryByList(pageDb.getRecords());
        return pageDb.setRecords(list);
    }

    /**
     * 获取分类树
     * @return 分类树
     */
    public JSONArray selectProductTree() {
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
                    int subClassId = subItem.getClassId();

                    JSONArray subProducts = createProductArray(subClassId);
                    JSONObject subObj = createObjectItem(subItem);
                    if(!subProducts.isEmpty()) {
                        subObj.put("children", subProducts);
                    }
                    subArray.add(subObj);
                }

                JSONObject obj = createObjectItem(item);
                obj.put("children", subArray);
                list.add(obj);
            } else {
                JSONArray subProducts = createProductArray(classId);
                JSONObject obj = createObjectItem(item);
                if(!subProducts.isEmpty()) {
                    obj.put("children", subProducts);
                }
                list.add(obj);
            }
        }
        return list;
    }
    private JSONObject createObjectItem(ProductClass item) {
        JSONObject obj = new JSONObject();
        obj.put("key", item.getClassId() + "");
        obj.put("label", item.getClassName());
        obj.put("value", item.getClassId() + "");
        obj.put("type", "class");
        return obj;
    }
    private JSONArray createProductArray(int classId) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.eq("class_id", classId);
        example.orderByDesc("create_time");
        List<Product> proList = productMapper.selectList(example);
        JSONArray list = new JSONArray();
        for (Product item : proList) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getProductId());
            obj.put("label", item.getProductName());
            obj.put("value", item.getProductId());
            obj.put("type", "product");
            list.add(obj);
        }
        return list;
    }

    /**
     * 判断产品ID是否存在
     * @param productId 产品ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotProductId(String productId) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        return !productMapper.exists(example);
    }

    /**
     * 查询产品
     * @param productId 产品ID
     * @return 产品信息
     */
    public Product find(@Param("productId") String productId) {
        Product product = productMapper.selectById(productId);
        associateQuery(product);
        return product;
    }

    /**
     * 查询产品
     * @param productId 产品ID
     * @return 产品信息
     */
    public Product findMeta(@Param("productId") String productId) {
        return productMapper.selectById(productId);
    }
    private void associateQuery(Product record) {
        record.setProductClass(productClassMapper.selectById(record.getClassId()));
        record.setFirmware(firmwareMapper.selectById(record.getFirmwareId()));
        record.setDrive(driveMapper.selectById(record.getDriveCode()));
    }
    private List<Product> associateQueryByList(List<Product> list) {
        for (int i = 0; i < list.size(); i++) {
            Product item = list.get(i);
            associateQuery(item);
            list.set(i, item);
        }
        return list;
    }

    /**
     * 添加产品
     * @param record 产品信息
     */
    public void insert(Product record) throws Exception {
        int classId = record.getClassId();

        ProductClass productClass = productClassMapper.selectById(classId);
        int levelId = productClass.getLevelId();
        String classRoute;
        if(levelId > 0) {
            classRoute = ":" + levelId + "::" + classId + ":";
        } else {
            classRoute = ":" + classId + ":";
        }
        record.setClassRoute(classRoute);
        record.setStatus(1);
        record.setCreateTime(new Date());
        int ret = productMapper.insert(record);
        if(ret == 0) {
            throw new Exception("添加产品信息失败");
        }
    }

    /**
     * 修改产品
     * @param record 产品信息
     */
    public void update(@Param("record") Product record) {
        String productId = record.getProductId();
        Product product = productMapper.selectById(productId);
        String oldProductName = product.getProductName();
        String newProductName = record.getProductName();
        int oldClassId = product.getClassId();
        int classId = record.getClassId();

        if(oldClassId != classId) {
            ProductClass productClass = productClassMapper.selectById(classId);
            int levelId = productClass.getLevelId();
            String classRoute;
            if(levelId > 0) {
                classRoute = ":" + levelId + "::" + classId + ":";
            } else {
                classRoute = ":" + classId + ":";
            }
            record.setClassRoute(classRoute);
            productMapper.updateById(record);

            QueryWrapper<Device> example = new QueryWrapper<>();
            example.eq("product_id", record.getProductId());
            List<Device> devList = deviceMapper.selectList(example);
            for (Device device : devList) {
                device.setClassRoute(classRoute);
                deviceMapper.updateById(device);
            }
        } else {
            productMapper.updateById(record);
        }

        if(!oldProductName.equals(newProductName)) {
            List<String> tables = iotStableMapper.selectStablesLike(productId);
            if(!tables.isEmpty()) {
                try {
                    iotStableMapper.modifyStableNotes(productId, newProductName);
                } catch (Exception e) {
                    logger.error("修改表{}备注名（{}）异常：{}", tables, newProductName, e.getMessage());
                }
            }
        }
    }

    /**
     * 批量转移产品分类
     * @param oldClassId 待转移分类ID
     * @param newClassId 目标分类ID
     */
    public void transferClass(int oldClassId, int newClassId) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.eq("class_id", oldClassId);
        List<Product> list = productMapper.selectList(example);
        for (Product product : list) {
            product.setClassId(newClassId);
            productMapper.updateById(product);
        }
    }

    /**
     * 删除产品 (删除前需验证是否存在设备)
     * @param productId 产品ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("productId") String productId) throws Exception {
        delByProductAuthCode(productId);
        delByAlarmModelRules(productId);
        delByProductModel(productId);
        dropIotTable(productId);
        return productMapper.deleteById(productId);
    }

    private void dropIotTable(String productId) {
        try {
            iotStableMapper.dropStable(productId);
        } catch (Exception e) {
            logger.error("删除表{}异常：{}", productId, e.getMessage());
        }
    }

    /**
     * 批量删除产品 (删除前需验证是否存在设备)
     * @param idList 产品ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<String> idList) throws Exception {
        delByProductAuthCodeArray(idList);
        delByAlarmModelRulesArray(idList);
        delByProductModelArray(idList);
        for (String id : idList) {
            dropIotTable(id);
        }
        return productMapper.deleteBatchIds(idList);
    }

    private void delByProductAuthCode(String productId) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        productAuthCodeMapper.delete(example);
    }

    private void delByProductAuthCodeArray(List<String> idList) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.in("product_id", idList);
        productAuthCodeMapper.delete(example);
    }

    private void delByProductModel(String productId) {
        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        productModeMapper.delete(example);
    }

    private void delByProductModelArray(List<String> idList) {
        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.in("product_id", idList);
        productModeMapper.delete(example);
    }

    private void delByAlarmModelRules(String productId) {
        QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
        exampleRule.eq("product_id", productId);
        modelAlarmRulesMapper.delete(exampleRule);
    }

    private void delByAlarmModelRulesArray(List<String> idList) {
        QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
        exampleRule.in("product_id", idList);
        modelAlarmRulesMapper.delete(exampleRule);
    }
}
