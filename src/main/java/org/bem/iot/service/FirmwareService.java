package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.FirmwareMapper;
import org.bem.iot.mapper.postgresql.FirmwareVersionMapper;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.model.general.Firmware;
import org.bem.iot.model.general.FirmwareVersion;
import org.bem.iot.model.product.Product;
import org.bem.iot.util.ConvertUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 固件
 * @author jakybland
 */
@Service
public class FirmwareService {
    @Resource
    FirmwareMapper firmwareMapper;

    @Resource
    FirmwareVersionMapper firmwareVersionMapper;

    @Resource
    ProductMapper productMapper;

    /**
     * 统计固件数量
     * @param example 统计条件
     * @return 固件数量
     */
    public long count(QueryWrapper<Firmware> example) {
        return firmwareMapper.selectCount(example);
    }

    /**
     * 查询固件列表
     * @param example 查询条件
     * @return 固件列表
     */
    public List<Firmware> select(QueryWrapper<Firmware> example) {
        List<Firmware> list = firmwareMapper.selectList(example);
        return getUrlToFirmwareList(list);
    }

    /**
     * 分页查询固件列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 固件列表
     */
    public IPage<Firmware> selectPage(QueryWrapper<Firmware> example, long index, long size) {
        Page<Firmware> page = new Page<>(index, size);
        IPage<Firmware> ipage = firmwareMapper.selectPage(page, example);
        List<Firmware> list = ipage.getRecords();
        ipage.setRecords(getUrlToFirmwareList(list));
        return ipage;
    }
    private List<Firmware> getUrlToFirmwareList(List<Firmware> list) {
        for (int i = 0; i < list.size(); i++) {
            Firmware firmware = list.get(i);
            int firmwareId = firmware.getFirmwareId();
            String version = firmware.getVersion();

            QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
            example.eq("firmware_id", firmwareId);
            example.eq("version", version);
            if (firmwareVersionMapper.exists(example)) {
                FirmwareVersion firmwareVersion = firmwareVersionMapper.selectOne(example);
                String url = firmwareVersion.getUrl();
                firmware.setUrl(url);
            } else {
                firmware.setUrl("");
            }
            list.set(i, firmware);
        }
        return list;
    }

    /**
     * 判断固件ID是否存在
     * @param firmwareId 固件ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotFirmwareId(int firmwareId) {
        QueryWrapper<Firmware> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        return !firmwareMapper.exists(example);
    }

    /**
     * 查询固件
     * @param firmwareId 固件ID
     * @return 固件信息
     */
    @Cacheable(value = "firmware", key = "#p0")
    public Firmware find(@Param("firmwareId") int firmwareId) {
        Firmware firmware = firmwareMapper.selectById(firmwareId);
        return getUrlToFirmware(firmware);
    }
    private Firmware getUrlToFirmware(Firmware firmware) {
        int firmwareId = firmware.getFirmwareId();
        String version = firmware.getVersion();

        QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        example.eq("version", version);
        if(firmwareVersionMapper.exists(example)) {
            FirmwareVersion firmwareVersion = firmwareVersionMapper.selectOne(example);
            String url = firmwareVersion.getUrl();
            firmware.setUrl(url);
        } else {
            firmware.setUrl("");
        }
        return firmware;
    }

    /**
     * 添加固件
     * @param record 固件信息
     * @throws Exception 异常信息
     */
    public void insert(Firmware record) throws Exception {
        String version = record.getVersion();
        String url = record.getUrl();
        record.setFirmwareId(null);
        int count = firmwareMapper.insert(record);
        if(count > 0) {
            int firmwareId = record.getFirmwareId();

            FirmwareVersion firmwareVersion = new FirmwareVersion();
            firmwareVersion.setFirmwareId(firmwareId);
            firmwareVersion.setVersion(version);
            firmwareVersion.setUrl(url);
            firmwareVersion.setReleaseTime(new Date());
            firmwareVersionMapper.insert(firmwareVersion);
        } else {
            throw new Exception("新增固件失败");
        }
    }

    /**
     * 修改固件
     * @param record 固件信息
     */
    @CachePut(value = "firmware", key = "#p0.firmwareId")
    public Firmware update(@Param("record") Firmware record) throws Exception {
        int firmwareId = record.getFirmwareId();
        String version = record.getVersion();

        Firmware firmware = firmwareMapper.selectById(firmwareId);
        String oldVersion = firmware.getVersion();
        boolean isUpdate = true;
        if (!version.equals(oldVersion)) {
            if(ConvertUtil.versionNotCompare(version, oldVersion)) {
                isUpdate = false;
            }
        }
        if(isUpdate) {
            String url = record.getUrl();
            int ret = firmwareMapper.updateById(record);
            if (ret >= 0) {
                if (!version.equals(oldVersion)) {
                    FirmwareVersion firmwareVersion = new FirmwareVersion();
                    firmwareVersion.setFirmwareId(firmwareId);
                    firmwareVersion.setVersion(version);
                    firmwareVersion.setUrl(url);
                    firmwareVersion.setReleaseTime(new Date());
                    firmwareVersionMapper.insert(firmwareVersion);
                }
                return record;
            } else {
                throw new Exception("修改固件失败");
            }
        } else {
            throw new Exception("新版本不能低于旧版本");
        }
    }

    /**
     * 删除固件 (删除前需验证是否关联产品)
     * @param firmwareId 固件ID
     * @return 删除数量
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "firmware", key = "#p0"),
                    @CacheEvict(value = "product", allEntries = true)
            }
    )
    public int del(@Param("firmwareId") int firmwareId) {
        removeByProduct(firmwareId);
        deleteByVersion(firmwareId);
        return firmwareMapper.deleteById(firmwareId);
    }

    /**
     * 批量删除固件 (删除前需验证关联产品)
     * @param idList 固件ID列表
     * @return 删除数量
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "firmware", allEntries = true),
                    @CacheEvict(value = "product", allEntries = true)
            }
    )
    public int delArray(List<Integer> idList) {
        removeArrayByProduct(idList);
        deleteArrayByVersion(idList);
        return firmwareMapper.deleteBatchIds(idList);
    }

    private void removeByProduct(int firmwareId) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        List<Product> list = productMapper.selectList(example);
        for (Product product : list) {
            product.setFirmwareId(0);
            productMapper.updateById(product);
        }
    }

    private void removeArrayByProduct(List<Integer> idList) {
        QueryWrapper<Product> example = new QueryWrapper<>();
        example.in("firmware_id", idList);
        List<Product> list = productMapper.selectList(example);
        for (Product product : list) {
            product.setFirmwareId(0);
            productMapper.updateById(product);
        }
    }

    private void deleteByVersion(int firmwareId) {
        QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
        example.eq("firmware_id", firmwareId);
        firmwareVersionMapper.delete(example);
    }

    private void deleteArrayByVersion(List<Integer> idList) {
        QueryWrapper<FirmwareVersion> example = new QueryWrapper<>();
        example.in("firmware_id", idList);
        firmwareVersionMapper.delete(example);
    }
}
