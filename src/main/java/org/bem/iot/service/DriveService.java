package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DriveMapper;
import org.bem.iot.mapper.postgresql.DriveParamsMapper;
import org.bem.iot.mapper.postgresql.ProtocolsMapper;
import org.bem.iot.model.general.Drive;
import org.bem.iot.model.general.DriveParams;
import org.bem.iot.model.general.Protocols;
import org.bem.iot.util.ConvertUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备驱动
 * @author JiangShiYi
 */
@Service
public class DriveService {
    @Resource
    DriveMapper driveMapper;

    @Resource
    ProtocolsMapper protocolsMapper;

    @Resource
    DriveParamsMapper driveParamsMapper;

    /**
     * 统计驱动数量
     * @param example 统计条件
     * @return 驱动数量
     */
    public long count(QueryWrapper<Drive> example) {
        return driveMapper.selectCount(example);
    }

    /**
     * 查询驱动列表
     * @param example 查询条件
     * @return 驱动列表
     */
    public List<Drive> select(QueryWrapper<Drive> example) {
        List<Drive> list = driveMapper.selectList(example);
        for (Drive drive : list) {
            String protocolName = getProtocolName(drive.getProtocolId());
            drive.setProtocolName(protocolName);
        }
        return list;
    }
    private String getProtocolName(int protocolId) {
        Protocols protocols = protocolsMapper.selectById(protocolId);
        if(protocols != null) {
            return protocols.getProtocolName();
        } else {
            return "";
        }
    }

    /**
     * 分页查询驱动列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 驱动列表
     */
    public IPage<Drive> selectPage(QueryWrapper<Drive> example, long index, long size) {
        Page<Drive> page = new Page<>(index, size);
        IPage<Drive> pageDb = driveMapper.selectPage(page, example);
        List<Drive> list = pageDb.getRecords();
        for (Drive drive : list) {
            String protocolName = getProtocolName(drive.getProtocolId());
            drive.setProtocolName(protocolName);
        }
        pageDb.setRecords(list);
        return pageDb;
    }

    /**
     * 判断驱动编号是否存在
     * @param driveCode 驱动编号
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotDriveCode(String driveCode) {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        return !driveMapper.exists(example);
    }

    /**
     * 查询驱动
     * @param driveCode 驱动编号
     * @return 驱动信息
     */
    @Cacheable(value = "device", key = "#p0")
    public Drive find(@Param("driveCode") String driveCode) {
        Drive drive = driveMapper.selectById(driveCode);
        String protocolName = getProtocolName(drive.getProtocolId());
        drive.setProtocolName(protocolName);
        return drive;
    }

    /**
     * 添加驱动
     * @param record 驱动信息
     * @throws Exception 异常信息
     */
    public void insert(Drive record) throws Exception {
        record.setDefaultDrive(0);
        record.setStatus(1);
        //获取驱动信息
        int count = driveMapper.insert(record);
        if(count < 1) {
            throw new Exception("新增驱动失败");
        } else {
            if(record.getStatus() == 2) {
                //启动驱动,加入驱动控制表
            }
        }
    }

    /**
     * 修改驱动
     * @param record 驱动信息
     */
    @CachePut(value = "device", key = "#p0.driveCode")
    public Drive update(@Param("record") Drive record) throws Exception {
        Drive device = driveMapper.selectById(record.getDriveCode());
        if(device.getDefaultDrive() == 0) {String version = record.getVersion();
            String oldVersion = device.getVersion();
            boolean isUpdate = true;
            if (!version.equals(oldVersion)) {
                if(ConvertUtil.versionNotCompare(version, oldVersion)) {
                    isUpdate = false;
                }
            }
            if(isUpdate) {
                device.setDriveName(record.getDriveName());
                device.setProtocolId(record.getProtocolId());
                device.setDriveSource(record.getDriveSource());
                device.setVersion(record.getVersion());
                device.setPackageUrl(record.getPackageUrl());
                device.setRemark(record.getRemark());
                device.setReleaseTime(record.getReleaseTime());
                driveMapper.updateById(record);
                return record;
            } else {
                throw new Exception("新版本号不能低于当前驱动版本号");
            }
        } else {
            return record;
        }
    }

    /**
     * 启动驱动
     * @param driveCode 驱动ID
     */
    @CachePut(value = "device", key = "#p0")
    public Drive starting(@Param("driveCode") String driveCode) {
        //启动驱动,加入驱动控制表
        //保存
        Drive record = driveMapper.selectById(driveCode);
        record.setStatus(2);
        driveMapper.updateById(record);
        return record;
    }

    /**
     * 停止驱动
     * @param driveCode 驱动ID
     */
    @CachePut(value = "device", key = "#p0")
    public Drive stoping(@Param("driveCode") String driveCode) {
        //停止驱动,移除驱动控制表
        //保存
        Drive record = driveMapper.selectById(driveCode);
        record.setStatus(1);
        driveMapper.updateById(record);
        return record;
    }

    /**
     * 删除驱动 (删除前需验证是否关联设备)
     * @param driveCode 驱动编号
     * @return 删除数量
     * @throws Exception 异常信息
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "device", key = "#p0"),
                    @CacheEvict(value = "device_params", allEntries = true)
            }
    )
    public int del(@Param("driveCode") String driveCode) throws Exception {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        example.eq("default_drive", 0);
        if(driveMapper.exists(example)) {
            //停止驱动,移除驱动控制表
            //删除
            delByParams(driveCode);
            return driveMapper.delete(example);
        } else {
            return 0;
        }
    }

    /**
     *
     * 批量删除驱动 (删除前需验证关联设备)
     * @param codeList 驱动编号列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "device", allEntries = true),
                    @CacheEvict(value = "device_params", allEntries = true)
            }
    )
    public int delArray(List<String> codeList) throws Exception {
        QueryWrapper<Drive> example = new QueryWrapper<>();
        example.in("drive_code", codeList);
        example.eq("default_drive", 0);
        if(driveMapper.exists(example)) {
            //批量停止驱动,移除驱动控制表
            //删除
            delByParamsArray(codeList);
            return driveMapper.delete(example);
        } else {
            return 0;
        }
    }

    private void delByParams(String driveCode) {
        QueryWrapper<DriveParams> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        driveParamsMapper.delete(example);
    }

    private void delByParamsArray(List<String> codeList) {
        QueryWrapper<DriveParams> example = new QueryWrapper<>();
        example.in("drive_code", codeList);
        driveParamsMapper.delete(example);
    }
}
