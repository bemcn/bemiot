package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.CertificateMapper;
import org.bem.iot.model.general.Certificate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 证书管理
 * @author jakybland
 */
@Service
public class CertificateService {
    @Resource
    CertificateMapper certificateMapper;

    /**
     * 统计证书数量
     * @param example 统计条件
     * @return 证书数量
     */
    public long count(QueryWrapper<Certificate> example) {
        return certificateMapper.selectCount(example);
    }

    /**
     * 查询证书列表
     * @param example 查询条件
     * @return 证书列表
     */
    public List<Certificate> select(QueryWrapper<Certificate> example) {
        return certificateMapper.selectList(example);
    }

    /**
     * 分页查询证书列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 证书列表
     */
    public IPage<Certificate> selectPage(QueryWrapper<Certificate> example, long index, long size) {
        Page<Certificate> page = new Page<>(index, size);
        return certificateMapper.selectPage(page, example);
    }

    /**
     * 判断证书ID是否存在
     * @param certificateId 证书ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotCertificateId(int certificateId) {
        QueryWrapper<Certificate> example = new QueryWrapper<>();
        example.eq("certificate_id", certificateId);
        return !certificateMapper.exists(example);
    }

    /**
     * 查询证书
     * @param certificateId 证书ID
     * @return 证书信息
     */
    public Certificate find(@Param("certificateId") int certificateId) {
        return certificateMapper.selectById(certificateId);
    }

    /**
     * 添加证书
     * @param record 证书信息
     */
    public void insert(Certificate record) {
        record.setCertificateId(null);
        certificateMapper.insert(record);
    }

    /**
     * 修改证书
     * @param record 证书信息
     */
    public Certificate update(@Param("record") Certificate record) {
        certificateMapper.updateById(record);
        return record;
    }

    /**
     * 删除证书 (删除前需验证是否存在用户)
     * @param certificateId 证书ID
     * @return 删除数量
     */
    public int del(@Param("certificateId") int certificateId) {
        return certificateMapper.deleteById(certificateId);
    }

    /**
     * 批量删除证书 (删除前需验证是否存在用户)
     * @param idList 证书ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        return certificateMapper.deleteBatchIds(idList);
    }
}
