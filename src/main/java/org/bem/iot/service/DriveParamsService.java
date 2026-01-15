package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.DriveParamsMapper;
import org.bem.iot.model.general.DriveParams;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 驱动参数参数
 * @author jakybland
 */
@Service
public class DriveParamsService {

    @Resource
    DriveParamsMapper driveParamsMapper;

    /**
     * 查询驱动参数列表
     * @param driveCode 驱动编号
     * @return 产品参数参数列表
     */
    public List<DriveParams> select(String driveCode, int groupType) {
        QueryWrapper<DriveParams> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        example.eq("group_type", groupType);
        example.orderByAsc("group_type", "order_num");
        return driveParamsMapper.selectList(example);
    }

    /**
     * 判断参数ID是否存在
     * @param paramsId 参数ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotParamsId(long paramsId) {
        QueryWrapper<DriveParams> example = new QueryWrapper<>();
        example.eq("params_id", paramsId);
        return !driveParamsMapper.exists(example);
    }

    /**
     * 判断参数标识是否存在
     * @param driveCode 驱动编号
     * @param paramsKey 参数标识
     * @return 存在返回false，不存在返回true
     */
    public boolean existsParamsKey(String driveCode, String paramsKey) {
        QueryWrapper<DriveParams> example = new QueryWrapper<>();
        example.eq("drive_code", driveCode);
        example.eq("params_key", paramsKey);
        return driveParamsMapper.exists(example);
    }

    /**
     * 获取参数信息
     * @param paramsId 参数ID
     * @return 参数信息
     */
    public DriveParams find(@Param("paramsId") long paramsId) {
        return driveParamsMapper.selectById(paramsId);
    }

    /**
     * 添加驱动参数
     * @param record 参数信息
     */
    public void insert(DriveParams record) {
        int orderNum = driveParamsMapper.selectMax(record.getDriveCode(), record.getGroupType()) + 1;

        record.setParamsId(null);
        record.setOrderNum(orderNum);
        driveParamsMapper.insert(record);
    }

    /**
     * 添加驱动参数
     * @param record 参数信息
     */
    public DriveParams update(@Param("record") DriveParams record) {
        DriveParams data = driveParamsMapper.selectById(record.getParamsId());
        data.setGroupType(record.getGroupType());
        data.setParamsName(record.getParamsName());
        data.setParamsType(record.getParamsType());
        data.setDefaultValue(record.getDefaultValue());
        data.setShowData(record.getShowData() == null ? "" : record.getShowData());
        driveParamsMapper.updateById(data);
        return record;
    }

    /**
     * 修改驱动参数排序
     * @param paramsId 参数id
     * @param orderNum 排序值
     */
    public DriveParams updateOrder(@Param("paramsId") long paramsId, int orderNum) {
        DriveParams record = driveParamsMapper.selectById(paramsId);
        String driveCode = record.getDriveCode();
        int groupType = record.getGroupType();
        int oldOrderNum = record.getOrderNum();
        if(orderNum != oldOrderNum) {
            QueryWrapper<DriveParams> example = new QueryWrapper<>();
            example.eq("drive_code", driveCode);
            example.eq("group_type", groupType);
            if (orderNum > oldOrderNum) {  //下移
                example.gt("order_num", oldOrderNum);
            } else { //上移
                example.ge("order_num", orderNum);
                example.lt("order_num", oldOrderNum);
            }
            List<DriveParams> list = driveParamsMapper.selectList(example);
            if (orderNum > oldOrderNum) {
                for (DriveParams data : list) {
                    data.setOrderNum(data.getOrderNum() - 1);
                    driveParamsMapper.updateById(data);
                }
            } else {
                for (DriveParams data : list) {
                    data.setOrderNum(data.getOrderNum() + 1);
                    driveParamsMapper.updateById(data);
                }
            }
            record.setOrderNum(orderNum);
            driveParamsMapper.updateById(record);
            return record;
        } else {
            return record;
        }
    }

    /**
     * 删除驱动参数
     * @param paramsId 参数ID
     * @return 删除数量
     */
    public int del(@Param("paramsId") long paramsId) {
        return driveParamsMapper.deleteById(paramsId);
    }

    /**
     * 批量删除驱动参数
     * @param idList 参数ID列表
     * @return 删除数量
     */
    public int delArray(List<Long> idList) {
        return driveParamsMapper.deleteBatchIds(idList);
    }
}
