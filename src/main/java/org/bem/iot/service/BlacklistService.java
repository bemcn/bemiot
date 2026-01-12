package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.entity.BlacklistImp;
import org.bem.iot.mapper.postgresql.BlacklistMapper;
import org.bem.iot.model.system.Blacklist;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 黑名单
 * @author jakybland
 */
@Service
public class BlacklistService {
    @Resource
    BlacklistMapper blacklistMapper;

    /**
     * 统计黑名单数量
     * @param example 查询条件
     * @return 黑名单数量
     */
    public long count(QueryWrapper<Blacklist> example) {
        return blacklistMapper.selectCount(example);
    }

    /**
     * 查询黑名单信息
     * @param example 查询条件
     * @return 黑名单信息列表
     */
    public List<Blacklist> select(QueryWrapper<Blacklist> example) {
        return blacklistMapper.selectList(example);
    }

    /**
     * 分页查询黑名单信息
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 黑名单信息列表
     */
    public IPage<Blacklist> selectPage(QueryWrapper<Blacklist> example, long index, long size) {
        Page<Blacklist> page = new Page<>(index, size);
        return blacklistMapper.selectPage(page, example);
    }

    /**
     * 判断黑名单ID是否存在
     * @param blackId 黑名单ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotBlackId(long blackId) {
        QueryWrapper<Blacklist> example = new QueryWrapper<>();
        example.eq("black_id", blackId);
        return !blacklistMapper.exists(example);
    }
    private boolean existsIpStart(String ipStart) {
        QueryWrapper<Blacklist> example = new QueryWrapper<>();
        example.eq("ip_start", ipStart);
        return blacklistMapper.exists(example);
    }
    private boolean existsIpEnd(String ipEnd) {
        QueryWrapper<Blacklist> example = new QueryWrapper<>();
        example.eq("ip_end", ipEnd);
        return blacklistMapper.exists(example);
    }

    /**
     * 验证是否不属于黑名单来源
     * @param clientIp 客户端IP地址
     * @return 存在返回false，不存在返回true
     */
    public boolean hasNotIpSource(String clientIp) {
        if("127.0.0.1".equals(clientIp)) {
            return true;
        } else {
            String[] ipItemNum = clientIp.split("\\.");
            String ipKey = ipItemNum[0] + "." + ipItemNum[1] + "." + ipItemNum[2];
            int ipLastNum = Integer.parseInt(ipItemNum[3]);

            QueryWrapper<Blacklist> example = new QueryWrapper<>();
            example.like("ip_source", ipKey);
            List<Blacklist> list = blacklistMapper.selectList(example);
            if(!list.isEmpty()) {
                boolean hasIp = false;
                for (Blacklist blacklist : list) {
                    String startIp = blacklist.getIpStart();
                    String endIp = blacklist.getIpEnd();
                    if(StrUtil.isEmpty(endIp)) {
                        if(clientIp.equals(startIp)) {
                            hasIp = true;
                            break;
                        }
                    } else {
                        String[] startIpArray = startIp.split("\\.");
                        String[] endIpArray = endIp.split("\\.");
                        int startIpNum = Integer.parseInt(startIpArray[3]);
                        int endIpNum = Integer.parseInt(endIpArray[3]);
                        if(ipLastNum >= startIpNum && ipLastNum <= endIpNum) {
                            hasIp = true;
                            break;
                        }
                    }
                }
                return !hasIp;
            } else {
                return true;
            }
        }
    }

    /**
     * 根据黑名单ID查询黑名单信息
     * @param blackId 黑名单ID
     * @return 黑名单信息
     */
    @Cacheable(value = "blacklist", key = "#p0")
    public Blacklist find(@Param("blackId") long blackId) {
        return blacklistMapper.selectById(blackId);
    }

    /**
     * 插入黑名单信息
     * @param record 黑名单信息
     * @throws Exception 异常信息
     */
    public void insert(Blacklist record) throws Exception {
        record.setBlackId(null);
        int ret = blacklistMapper.insert(record);
        if(ret < 1) {
            throw new Exception("新增黑名单失败");
        }
    }

    /**
     * 导入黑名单信息
     * @param blacklist 待导入单条黑名单信息
     * @return 成功返回true
     */
    public boolean insertImport(BlacklistImp blacklist) {
        try {
            if(StrUtil.isEmpty(blacklist.getIpStart())) {
                return false;
            } else if(existsIpStart(blacklist.getIpStart())) {
                return false;
            } else if(StrUtil.isNotEmpty(blacklist.getIpStart()) && existsIpEnd(blacklist.getIpEnd())) {
                return false;
            } else {
                Date createTime = new Date();

                Blacklist record = new Blacklist();
                record.setBlackId(null);
                record.setIpStart(blacklist.getIpStart());
                record.setIpEnd(blacklist.getIpEnd());
                record.setRemark(blacklist.getRemark());
                record.setCreateTime(createTime);

                int ret = blacklistMapper.insert(record);
                return ret > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 修改黑名单信息
     * @param record 黑名单信息
     * @return 返回黑名单信息
     */
    @CachePut(value = "blacklist", key = "#p0.blackId")
    public Blacklist update(@Param("record") Blacklist record) {
        blacklistMapper.updateById(record);
        return record;
    }

    /**
     * 删除黑名单信息
     * @param blackId 黑名单ID
     * @return 删除数量
     */
    @CacheEvict(value = "blacklist", key = "#p0")
    public int del(@Param("blackId") long blackId) {
        return blacklistMapper.deleteById(blackId);
    }

    /**
     * 批量删除黑名单信息
     * @param idList 黑名单ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    @CacheEvict(value = "blacklist", allEntries = true)
    public int delArray(List<Integer> idList) throws Exception {
        QueryWrapper<Blacklist> example = new QueryWrapper<>();
        example.in("black_id", idList);
        return blacklistMapper.delete(example);
    }
}
