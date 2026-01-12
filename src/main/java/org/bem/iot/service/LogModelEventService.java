package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.mapper.tdengine.LogModelEventMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.log.LogModelEvent;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物模型事件日志
 * @author jakybland
 */
@Service
public class LogModelEventService {
    @Resource
    LogModelEventMapper logModelEventMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    ProductModelMapper productModelMapper;

    /**
     * 获取统计数量
     * @param example 查询条件
     * @return 返回数量值
     */
    public long count(QueryWrapper<LogModelEvent> example) {
        return logModelEventMapper.selectCount(example);
    }

    /**
     * 查询指定数量的数据
     * @param example 查询条件
     * @param size 显示数量
     * @return 符合条件的数据列表
     */
    public List<LogModelEvent> selectLimit(QueryWrapper<LogModelEvent> example, long size) {
        IPage<LogModelEvent> page = new Page<>(1L, size);
        IPage<LogModelEvent> result = logModelEventMapper.selectPage(page, example);
        List<LogModelEvent> list = result.getRecords();
        for (LogModelEvent event : list) {
            String deviceId = event.getDeviceId();
            String productId = event.getProductId();
            String modelIdentity = event.getModelIdentity();

            Device device = deviceMapper.selectById(deviceId);
            Product product = productMapper.selectById(productId);

            QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
            exampleModel.eq("product_id", productId);
            exampleModel.eq("model_identity", modelIdentity);
            ProductModel model = productModelMapper.selectOne(exampleModel);

            event.setDevice(device);
            event.setProduct(product);
            event.setModel(model);
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
    public IPage<LogModelEvent> selectPage(QueryWrapper<LogModelEvent> example, long index, long size) {
        Page<LogModelEvent> page = new Page<>(index, size);
        IPage<LogModelEvent> result = logModelEventMapper.selectPage(page, example);
        List<LogModelEvent> list = result.getRecords();
        for (LogModelEvent event : list) {
            String deviceId = event.getDeviceId();
            String productId = event.getProductId();
            String modelIdentity = event.getModelIdentity();

            Device device = deviceMapper.selectById(deviceId);
            Product product = productMapper.selectById(productId);

            QueryWrapper<ProductModel> exampleModel = new QueryWrapper<>();
            exampleModel.eq("product_id", productId);
            exampleModel.eq("model_identity", modelIdentity);
            ProductModel model = productModelMapper.selectOne(exampleModel);

            event.setDevice(device);
            event.setProduct(product);
            event.setModel(model);
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 插入新的数据
     * @param record 新数据
     */
    public void insert(LogModelEvent record) {
        logModelEventMapper.insert(record);
    }

    /**
     * 删除数据
     * @param id 删除的ID
     */
    public void del(@Param("id") String id) {
        QueryWrapper<LogModelEvent> example = new QueryWrapper<>();
        example.eq("id", id);
        logModelEventMapper.delete(example);
    }

    /**
     * 批量删除数据
     * @param idList 删除的ID列表
     */
    public void delArray(List<String> idList) {
        QueryWrapper<LogModelEvent> example = new QueryWrapper<>();
        example.in("id", idList);
        logModelEventMapper.delete(example);
    }
}
