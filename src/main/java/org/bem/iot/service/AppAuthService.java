package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.AppAuthMapper;
import org.bem.iot.model.general.AppAuth;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运用授权
 * @author jakybland
 */
@Service
public class AppAuthService {
    @Resource
    AppAuthMapper appAuthMapper;

    /**
     * 统计运用授权信息数量
     * @param example 查询条件
     * @return 运用授权信息数量
     */
    public long count(QueryWrapper<AppAuth> example) {
        return appAuthMapper.selectCount(example);
    }

    /**
     * 查询运用授权信息信息
     * @param example 查询条件
     * @return 运用授权信息信息列表
     */
    public List<AppAuth> select(QueryWrapper<AppAuth> example) {
        return appAuthMapper.selectList(example);
    }

    /**
     * 根据ID列表查询运用授权信息信息
     * @param idList 运用授权信息ID列表
     * @return 运用授权信息信息列表
     */
    public List<AppAuth> selectIds(List<String> idList) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.in("app_id", idList);
        return appAuthMapper.selectList(example);
    }

    /**
     * 分页查询运用授权信息信息
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 运用授权信息信息列表
     */
    public IPage<AppAuth> selectPage(QueryWrapper<AppAuth> example, long index, long size) {
        Page<AppAuth> page = new Page<>(index, size);
        return appAuthMapper.selectPage(page, example);
    }

    /**
     * 判断应用ID是否存在
     * @param appId 应用ID
     * @return 存在返回true，不存在返回false
     */
    public boolean existsNotApp(String appId) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.eq("app_id", appId);
        return !appAuthMapper.exists(example);
    }

    /**
     * 判断应用ID是否存在
     * @param appId 应用ID
     * @param type 类型 user / system
     * @return 存在返回true，不存在返回false
     */
    public boolean existsNotApp(String appId, String type) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.eq("app_id", appId);
        example.eq("app_auth", type);
        return !appAuthMapper.exists(example);
    }

    /**
     * 判断类型是否正确
     * @param type 类型 user / system
     * @return 正确 true
     */
    public boolean existsType(String type) {
        return "user".equals(type) || "system".equals(type);
    }

    /**
     * 根据运用ID查询运用授权信息信息
     * @param appId 运用ID
     * @return 运用授权信息信息
     */
    @Cacheable(value = "app_auth", key = "#p0")
    public AppAuth find(@Param("appId") String appId) {
        return appAuthMapper.selectById(appId);
    }

    /**
     * 插入运用授权信息信息
     * @param record 运用授权信息信息
     * @throws Exception 异常信息
     */
    public void insert(AppAuth record) throws Exception {
        int count = appAuthMapper.insert(record);
        if(count < 1) {
            throw new Exception("新增应用接入失败");
        }
    }

    /**
     * 修改运用授权信息信息
     * @param record 运用授权信息信息
     */
    @CachePut(value = "app_auth", key = "#p0.appId")
    public AppAuth update(@Param("record") AppAuth record) {
        appAuthMapper.updateById(record);
        return record;
    }

    /**
     * 删除运用授权信息信息
     * @param appId 运用授权信息ID
     * @return 返回删除数量
     */
    @CacheEvict(value = "app_auth", key = "#p0")
    public int del(@Param("appId") String appId) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.eq("app_id", appId);
        example.eq("is_system", 0);
        return appAuthMapper.deleteById(appId);
    }

    /**
     * 批量删除运用授权信息信息
     * @param idList 运用授权信息ID列表
     * @return 返回删除数量
     */
    @CacheEvict(value = "app_auth", allEntries = true)
    public int delArray(List<String> idList) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.in("app_id", idList);
        example.eq("is_system", 0);
        return appAuthMapper.deleteBatchIds(idList);
    }
}
