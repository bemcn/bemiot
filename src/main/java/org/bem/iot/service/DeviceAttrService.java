package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DeviceAttrMapper;
import org.bem.iot.mapper.postgresql.DeviceGroupMapper;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ProductAttrMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceAttr;
import org.bem.iot.model.device.DeviceGroup;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductAttr;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 档案属性
 * @author jakybland
 */
@Service
public class DeviceAttrService {
    @Resource
    DeviceAttrMapper deviceAttrMapper;

    @Resource
    ProductAttrMapper productAttrMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    ProductService productService;

    @Resource
    DeviceGroupMapper deviceGroupMapper;

    /**
     * 统计档案属性数量
     * @param example 统计条件
     * @return 档案属性数量
     */
    public long count(QueryWrapper<DeviceAttr> example) {
        return deviceAttrMapper.selectCount(example);
    }

    /**
     * 查询档案属性列表
     * @param deviceId 设备ID
     * @return 档案属性列表
     */
    public List<DeviceAttr> select(String deviceId) {
        QueryWrapper<DeviceAttr> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        example.orderByAsc("attr_id");
        List<DeviceAttr> list = deviceAttrMapper.selectList(example);
        if(list.isEmpty()) {
            Device device = deviceMapper.selectById(deviceId);
            String productId = device.getProductId();

            QueryWrapper<ProductAttr> examplePro = new QueryWrapper<>();
            examplePro.eq("product_id", productId);
            examplePro.orderByAsc("attr_id");
            List<ProductAttr> listPro = productAttrMapper.selectList(examplePro);
            if(!listPro.isEmpty()) {
                for(ProductAttr attr : listPro) {
                    DeviceAttr record = new DeviceAttr();
                    record.setDeviceId(deviceId);
                    record.setAttrId(attr.getAttrId());
                    record.setClassRoute(attr.getClassRoute());
                    record.setFieldKey(attr.getFieldKey());
                    record.setFieldLabel(attr.getFieldLabel());
                    record.setFieldType(attr.getFieldType());
                    if("date".equals(attr.getFieldType())) {
                        record.setFieldValue("2025-01-01");
                    } else {
                        record.setFieldValue("");
                    }
                    list.add(record);
                }
            }
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
    public IPage<DeviceAttr> selectPage(QueryWrapper<DeviceAttr> example, long index, long size) {
        Page<DeviceAttr> page = new Page<>(index, size);
        IPage<DeviceAttr> pageData = deviceAttrMapper.selectPage(page, example);
        List<DeviceAttr> list = pageData.getRecords();
        for (DeviceAttr attr : list) {
            String deviceId = attr.getDeviceId();
            Device device = relevancyDevice(deviceId);
            attr.setDevice(device);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 查询档案属性
     * @param devAttrId 档案属性ID
     * @return 档案属性信息
     */
    public DeviceAttr find(@Param("devAttrId") long devAttrId) {
        DeviceAttr data = deviceAttrMapper.selectById(devAttrId);
        Device device = relevancyDevice(data.getDeviceId());
        data.setDevice(device);
        return data;
    }

    private Device relevancyDevice(String deviceId) {
        Device device = deviceMapper.selectById(deviceId);

        Product product = productService.find(device.getProductId());
        DeviceGroup group;
        if(device.getGroupId() > 0) {
            group = deviceGroupMapper.selectById(device.getGroupId());
        } else {
            group = null;
        }

        device.setProduct(product);
        device.setGroup(group);
        return device;
    }

    /**
     * 批量添加档案属性
     * @param deviceId 设备ID
     * @param recordArray 属性值集合
     */
    public void insertArray(String deviceId, JSONArray recordArray) {
        Device device = deviceMapper.selectById(deviceId);
        String classRoute = device.getClassRoute();

        deviceAttrMapper.delete(new QueryWrapper<DeviceAttr>().eq("device_id", deviceId));

        for (Object obj : recordArray) {
            JSONObject jsonObj = (JSONObject) obj;
            String id = InternalIdUtil.createUUID();

            DeviceAttr record = new DeviceAttr();
            record.setDevAttrId(id);
            record.setAttrId(jsonObj.getLong("attrId"));
            record.setDeviceId(deviceId);
            record.setClassRoute(classRoute);
            record.setFieldKey(jsonObj.getString("fieldKey"));
            record.setFieldLabel(jsonObj.getString("fieldLabel"));
            record.setFieldType(jsonObj.getString("fieldType"));
            record.setFieldValue(jsonObj.getString("fieldValue"));
            deviceAttrMapper.insert(record);
        }
    }
}
