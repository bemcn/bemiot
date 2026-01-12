package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.DeviceMapper;
import org.bem.iot.mapper.postgresql.ProductAuthCodeMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductAuthCode;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 产品码授权码
 * @author jakybland
 */
@Service
public class ProductAuthCodeService {
    @Resource
    ProductAuthCodeMapper productAuthCodeMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    /**
     * 统计授权码数量
     * @param example 统计条件
     * @return 授权码数量
     */
    public long count(QueryWrapper<ProductAuthCode> example) {
        return productAuthCodeMapper.selectCount(example);
    }

    /**
     * 分页查询授权码列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 授权码列表
     */
    public IPage<ProductAuthCode> selectPage(QueryWrapper<ProductAuthCode> example, long index, long size) {
        Page<ProductAuthCode> page = new Page<>(index, size);
        IPage<ProductAuthCode> result = productAuthCodeMapper.selectPage(page, example);
        List<ProductAuthCode> list = result.getRecords();
        for (ProductAuthCode record : list) {
            String productId = record.getProductId();
            String deviceId = record.getDeviceId();
            Integer userId = record.getUserId();

            Product product = productMapper.selectById(productId);
            record.setProduct(product);

            if (StrUtil.isNotEmpty(deviceId)) {
                Device device = deviceMapper.selectById(deviceId);
                record.setDevice(device);
            }
            if (userId != null && userId > 0) {
                UserInfo user = userInfoMapper.selectById(userId);
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userName", user.getUserName());
                userMap.put("nickName", user.getNickName());
                userMap.put("headImg", user.getHeadImg());
                record.setUser(userMap);
            }
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 获取未绑定授权码设备列表
     * @return 授权码列表
     */
    public IPage<Device> selectNotBindDevicePageList(long index, long size) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("device_id", 0);
        List<ProductAuthCode> list = productAuthCodeMapper.selectList(example);
        List<String> devIdList = list.stream().map(ProductAuthCode::getDeviceId).toList();

        Page<Device> page = new Page<>(index, size);

        QueryWrapper<Device> exampleDev = new QueryWrapper<>();
        exampleDev.notIn("device_id", devIdList);
        return deviceMapper.selectPage(page, exampleDev);
    }

    /**
     * 判断授权码是否存在
     * @param authCode 授权码
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotAuthCode(String authCode) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("auth_code", authCode);
        return !productAuthCodeMapper.exists(example);
    }

    /**
     * 判断授权码是否存在
     * @param deviceId 设备Id
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotAuthCodeByDev(String deviceId) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        return !productAuthCodeMapper.exists(example);
    }

    /**
     * 查询授权码
     * @param authCode 授权码
     * @return 授权码信息
     */
    public ProductAuthCode find(String authCode) {
        ProductAuthCode record = productAuthCodeMapper.selectById(authCode);
        String productId = record.getProductId();
        String deviceId = record.getDeviceId();
        Integer userId = record.getUserId();

        Product product = productMapper.selectById(productId);
        record.setProduct(product);

        if (StrUtil.isNotEmpty(deviceId)) {
            Device device = deviceMapper.selectById(deviceId);
            record.setDevice(device);
        }
        if (userId != null && userId > 0) {
            UserInfo user = userInfoMapper.selectById(userId);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("headImg", user.getHeadImg());
            record.setUser(userMap);
        }
        return record;
    }

    public ProductAuthCode findMeta(String authCode) {
        return productAuthCodeMapper.selectById(authCode);
    }

    /**
     * 查询授权码
     * @param deviceId 设备ID
     * @return 授权码信息
     */
    public ProductAuthCode findByDev(String deviceId) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("device_id", deviceId);
        ProductAuthCode record = productAuthCodeMapper.selectOne(example);
        String productId = record.getProductId();
        Integer userId = record.getUserId();

        Product product = productMapper.selectById(productId);
        record.setProduct(product);

        Device device = deviceMapper.selectById(deviceId);
        record.setDevice(device);

        if (userId != null && userId > 0) {
            UserInfo user = userInfoMapper.selectById(userId);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("headImg", user.getHeadImg());
            record.setUser(userMap);
        }
        return record;
    }

    /**
     * 生成授权码
     * @param productId 产品ID
     * @param number 生成数量
     */
    public void createNumber(String productId, int number) {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        for(int i = 0; i < number; i++) {
            int index = i + 1;
            String authCode = InternalIdUtil.createProductAuthCode(timestamp, index, uuid);
            Date date = new Date();

            ProductAuthCode record = new ProductAuthCode();
            record.setAuthCode(authCode);
            record.setProductId(productId);
            record.setDeviceId("");
            record.setUserId(0);
            record.setStatus(1);
            record.setRemark("");
            record.setCreateTime(date);
            productAuthCodeMapper.insert(record);
        }
    }

    /**
     * 授权码绑定
     * @param authCode 授权码
     * @param deviceId 设备ID
     * @param userId 用户ID
     * @param remark 备注
     */
    public void bindAuthCode(String authCode, String deviceId, int userId, String remark) {
        ProductAuthCode record = productAuthCodeMapper.selectById(authCode);
        record.setDeviceId(deviceId);
        record.setUserId(userId);
        record.setStatus(1);
        record.setRemark(remark);
        productAuthCodeMapper.updateById(record);
    }

    /**
     * 取消授权码绑定用户
     * @param authCode 授权码
     */
    public void cancelBindUser(String authCode) {
        ProductAuthCode record = productAuthCodeMapper.selectById(authCode);
        record.setUserId(0);
        productAuthCodeMapper.updateById(record);
    }

    /**
     * 取消授权码使用
     * @param authCode 授权码
     */
    public void cancelBind(String authCode) {
        ProductAuthCode record = productAuthCodeMapper.selectById(authCode);
        record.setDeviceId("");
        record.setUserId(0);
        record.setStatus(0);
        record.setRemark("");
        productAuthCodeMapper.updateById(record);
    }

    /**
     * 删除授权码
     * @param authCode 授权码
     * @return 删除数量
     */
    public int del(String authCode) {
        return productAuthCodeMapper.deleteById(authCode);
    }

    /**
     * 删除授权码
     * @param authCodes 授权码集合
     * @return 删除数量
     */
    public int delArray(List<String> authCodes) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.in("auth_code", authCodes);
        return productAuthCodeMapper.delete(example);
    }
}
