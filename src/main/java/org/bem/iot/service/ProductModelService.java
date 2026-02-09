package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ModelAlarmRulesMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.mapper.tdengine.IotStableMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.ModelAlarmRules;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.bem.iot.util.InternalIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 物模型
 * @author jakybland
 */
@Service
public class ProductModelService {
    @Resource
    ProductModelMapper productModelMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    IotStableMapper iotStableMapper;

    @Resource
    ModelAlarmRulesMapper modelAlarmRulesMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProductModelService.class);


    /**
     * 查询物模型列表
     * @param example 查询条件
     * @return 物模型列表
     */
    public List<ProductModel> select(QueryWrapper<ProductModel> example) {
        List<ProductModel> list = productModelMapper.selectList(example);
        for (ProductModel item : list) {
            modelAlarmCount(item);
        }
        return list;
    }

    /**
     * 查询设备物模型列表
     * @param example 查询条件
     * @param deviceId 设备ID
     * @return 物模型列表
     */
    public List<ProductModel> selectDeviceModels(QueryWrapper<ProductModel> example, String deviceId) {
        int online;
        String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + deviceId);
        if(strOnline != null) {
            online = Integer.parseInt(strOnline);
        } else {
            online = 0;
        }

        List<ProductModel> list = productModelMapper.selectList(example);
        for (ProductModel item : list) {
            String modelIdentity = item.getModelIdentity();
            realTimeValue(deviceId, modelIdentity, item, online);
        }
        return list;
    }

    /**
     * 查询设备物模型列表
     * @param example 查询条件
     * @param deviceId 设备ID
     * @param index 页码
     * @param size 每页数量
     * @return 物模型列表
     */
    public IPage<ProductModel> selectDeviceModelsPage(QueryWrapper<ProductModel> example, String deviceId, long index, long size) {
        Page<ProductModel> page = new Page<>(index, size);
        int online;
        String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + deviceId);
        if(strOnline != null) {
            online = Integer.parseInt(strOnline);
        } else {
            online = 0;
        }
        IPage<ProductModel> pageDb = productModelMapper.selectPage(page, example);
        List<ProductModel> list = pageDb.getRecords();
        for (ProductModel item : list) {
            String modelIdentity = item.getModelIdentity();
            realTimeValue(deviceId, modelIdentity, item, online);
        }
        pageDb.setRecords(list);
        return pageDb;
    }

    /**
     * 判断物模型ID不存在
     * @param modelId 物模型ID
     * @return 不存在返回false，存在返回true
     */
    public boolean existsNotModelId(String modelId) {
        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("model_id", modelId);
        return !productModelMapper.exists(example);
    }

    /**
     * 判断物模型标识不存在
     * @param productId 产品ID
     * @param modelIdentity 物模型标识
     * @return 不存在返回false，存在返回true
     */
    public boolean existsModelIdentity(String productId, String modelIdentity) {
        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        example.eq("model_identity", modelIdentity);
        return productModelMapper.exists(example);
    }

    /**
     * 查询物模型
     * @param modelId 物模型ID
     * @return 通用物模型信息
     */
    public ProductModel find(String modelId) {
        ProductModel item = productModelMapper.selectById(modelId);
        modelAlarmCount(item);
        return item;
    }

    /**
     * 查询物模型
     * @param deviceId 设备ID
     * @param identity 物模型标识
     * @return 通用物模型信息
     */
    public ProductModel findDeviceModel(String deviceId, String identity) {
        Device device = deviceMapper.selectById(deviceId);
        String productId = device.getProductId();

        int online;
        String strOnline = stringRedisTemplate.opsForValue().get("device_online:" + deviceId);
        if(strOnline != null) {
            online = Integer.parseInt(strOnline);
        } else {
            online = 0;
        }

        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        example.eq("model_identity", identity);
        long count = productModelMapper.selectCount(example);
        if(count > 0) {
            ProductModel data = productModelMapper.selectOne(example);
            realTimeValue(deviceId, identity, data, online);
            return data;
        } else {
            return null;
        }
    }
    private void modelAlarmCount(ProductModel data) {
        QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
        exampleRule.eq("product_id", data.getProductId());
        exampleRule.eq("model_identity", data.getModelIdentity());
        long alarmRileCount = modelAlarmRulesMapper.selectCount(exampleRule);
        data.setAlarmRileCount(alarmRileCount);
    }
    private void realTimeValue(String deviceId, String identity, ProductModel data, int online) {
        QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
        exampleRule.eq("product_id", data.getProductId());
        exampleRule.eq("model_identity", identity);
        long alarmRileCount = modelAlarmRulesMapper.selectCount(exampleRule);

        String dataType = data.getDataType();
        String key = deviceId + ":" + identity + ":value";
        String value = "";
        int status = 0;
        if(online == 0) {
            status = 1;
            if("bool".equals(dataType)) {
                value = "false";
            } else {
                value = "null";
            }
        } else {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                value = stringRedisTemplate.opsForValue().get(key);
            } else {
                if("bool".equals(dataType)) {
                    value = "false";
                } else {
                    value = "null";
                }
            }
        }
        data.setValue(value);
        data.setStatus(status);
        data.setAlarmRileCount(alarmRileCount);
    }

    /**
     * 新增物模型
     * @param record 物模型信息
     */
    public void insert(ProductModel record) {
        String modelId = InternalIdUtil.createUUID();
        record.setModelId(modelId);
        record.setCreateTime(new Date());
        int count = productModelMapper.insert(record);
        if(count > 0) {
            String productId = record.getProductId();
            QueryWrapper<ProductModel> example = new QueryWrapper<>();
            example.eq("product_id", productId);
            example.eq("history", 1);
            long num = productModelMapper.selectCount(example);
            if(num > 0L) {
                List<String> tables = iotStableMapper.selectStablesLike(productId);
                if(tables.isEmpty()) {
                    createIotTable(productId);
                } else {
                    String dataType = record.getDataType();
                    String modelIdentity = record.getModelIdentity();//maxLength
                    String dataDefinition = record.getDataDefinition();
                    JSONObject definition = JSONObject.parseObject(dataDefinition);
                    int modelClass = record.getModelClass();
                    if(modelClass == 1) {
                        addIotTableColumn(productId, modelIdentity, dataType, definition);
                    }

                }
            }
        }
    }
    private void createIotTable(String productId) {
        Product product = productMapper.selectById(productId);
        String productName = product.getProductName();

        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        example.in("model_class", 1);
        List<ProductModel> modelList = productModelMapper.selectList(example);

        StringBuilder fileds = new StringBuilder("ts timestamp");
        for (ProductModel item : modelList) {
            String modelIdentity = item.getModelIdentity();
            String dataType = item.getDataType();

            switch (dataType) {
                case "int":
                case "enum":
                    fileds.append(", ").append(modelIdentity).append(" int");
                    break;
                case "number":
                    fileds.append(", ").append(modelIdentity).append(" float");
                    break;
                case "timestamp":
                    fileds.append(", ").append(modelIdentity).append(" timestamp");
                    break;
                case "bool":
                    fileds.append(", ").append(modelIdentity).append(" bool");
                    break;
                default:
                    fileds.append(", ").append(modelIdentity).append(" varchar(255)");
                    break;
            }
        }
        String tags = "gatewayId varchar(50), spaceRoute varchar(255)";
        try {
            iotStableMapper.createStable(productId, fileds.toString(), tags, productName);
        } catch (Exception e) {
            logger.error("创建物模型{}表异常：{}", productId, e.getMessage());
        }
    }
    private void addIotTableColumn(String productId, String modelIdentity, String dataType, JSONObject definition) {
        String columnType = "";
        if("int".equals(dataType) || "enum".equals(dataType)) {
            columnType = "INT";
        } else if("number".equals(dataType)) {
            columnType = "FLOAT";
        } else if("timestamp".equals(dataType)) {
            columnType = "TIMESTAMP";
        } else if("bool".equals(dataType)) {
            columnType = "BOOL";
        } else if("text".equals(dataType)) {
            String maxLengthStr = definition.getString("maxLength");
            int maxLength = Integer.parseInt(maxLengthStr) * 2;
            columnType = "NCHAR(" + maxLength + ")";
        } else {
            columnType = "NCHAR(500)";
        }
        try {
            iotStableMapper.addStableColumn(productId, modelIdentity, columnType);
        } catch (Exception e) {
            logger.error("新增物模型{}字段{}异常：{}", productId, modelIdentity, e.getMessage());
        }
    }

    /**
     * 修改物模型
     * @param record 物模型信息
     */
    public void update(ProductModel record) {
        String modelId = record.getModelId();
        ProductModel oldModel = productModelMapper.selectById(modelId);
        String productId = oldModel.getProductId();
        String modelIdentity = record.getModelIdentity();
        String dataType = record.getDataType();
        int newClass = record.getModelClass();
        int oldClass = oldModel.getModelClass();
        int newHistory = record.getHistory();
        int oldHistory = oldModel.getHistory();
        JSONObject definition = JSONObject.parseObject(record.getDataDefinition());

        if(newHistory != oldHistory) {
            if(newHistory == 0 && oldHistory == 1) {
                if(oldClass < 3) {
                    try {
                        iotStableMapper.dropStableColumn(productId, modelIdentity);
                    } catch (Exception e) {
                        logger.error("新增物模型{}字段{}异常：{}", productId, modelIdentity, e.getMessage());
                    }
                }
            } else {
                if(oldClass > 2 && newClass < 3) {
                    addIotTableColumn(productId, modelIdentity, dataType, definition);
                } else if(oldClass < 3 && newClass > 2) {
                    try {
                        iotStableMapper.dropStableColumn(productId, modelIdentity);
                    } catch (Exception e) {
                        logger.error("新增物模型{}字段{}异常：{}", productId, modelIdentity, e.getMessage());
                    }
                }
            }
        } else {
            if(newHistory == 1) {
                if(oldClass > 2 && newClass < 3) {
                    addIotTableColumn(productId, modelIdentity, dataType, definition);
                } else if(oldClass < 3 && newClass > 2) {
                    try {
                        iotStableMapper.dropStableColumn(productId, modelIdentity);
                    } catch (Exception e) {
                        logger.error("新增物模型{}字段{}异常：{}", productId, modelIdentity, e.getMessage());
                    }
                }
            }
        }
        productModelMapper.updateById(record);
    }



    /**
     * 删除物模型
     * @param modelId 物模型ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(String modelId) throws Exception {
        ProductModel model = productModelMapper.selectById(modelId);
        if(model == null) {
            return 0;
        } else {
            String productId = model.getProductId();
            String modelIdentity = model.getModelIdentity();
            int count = productModelMapper.deleteById(modelId);
            if (count > 0) {
                delIotSColumn(productId, modelIdentity);
            }

            QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
            exampleRule.eq("product_id", productId);
            exampleRule.eq("model_identity", modelIdentity);
            modelAlarmRulesMapper.delete(exampleRule);

            return count;
        }
    }
    private void delIotSColumn(String productId, String modelIdentity) {
        QueryWrapper<ProductModel> example = new QueryWrapper<>();
        example.eq("product_id", productId);
        long num = productModelMapper.selectCount(example);
        // 如果没有物模型则删除物模型表，如果有则删除字段
        if (num == 0) {
            try {
                iotStableMapper.dropStable(productId);
            } catch (Exception e) {
                logger.error("删除物模型表{}异常：{}", productId, e.getMessage());
            }
        } else {
            try {
                iotStableMapper.dropStableColumn(productId, modelIdentity);
            } catch (Exception e) {
                logger.error("删除物模型表{}中{}列异常：{}", productId, modelIdentity, e.getMessage());
            }
        }
    }

    /**
     * 批量删除物模型 (删除前需验证是否存在设备)
     * @param idList 物模型ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<String> idList) throws Exception {
        int count = productModelMapper.deleteBatchIds(idList);
        for (String modelId : idList) {
            ProductModel model = productModelMapper.selectById(modelId);
            if(model != null) {
                delIotSColumn(model.getProductId(), model.getModelIdentity());

                QueryWrapper<ModelAlarmRules> exampleRule = new QueryWrapper<>();
                exampleRule.eq("product_id", model.getProductId());
                exampleRule.eq("model_identity", model.getModelIdentity());
                modelAlarmRulesMapper.delete(exampleRule);
            }
        }
        return count;
    }
}