package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.VideoServerMapper;
import org.bem.iot.model.video.VideoServer;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 视频服务
 * @author jakybland
 */
@Service
public class VideoServerService {
    @Resource
    VideoServerMapper videoServerMapper;

    /**
     * 统计视频服务数量
     * @param example 查询条件
     * @return 视频服务数量
     */
    public long count(QueryWrapper<VideoServer> example) {
        return videoServerMapper.selectCount(example);
    }

    /**
     * 查询视频服务信息
     * @param example 查询条件
     * @return 视频服务信息列表
     */
    public List<VideoServer> select(QueryWrapper<VideoServer> example) {
        List<VideoServer> list = videoServerMapper.selectList(example);
        for (VideoServer server : list) {
            String serviceConfig = server.getServiceConfig();
            JSONObject config = JSONObject.parseObject(serviceConfig);
            server.setConfig(config);
        }
        return list;
    }

    /**
     * 分页查询视频服务信息
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 视频服务信息列表
     */
    public IPage<VideoServer> selectPage(QueryWrapper<VideoServer> example, long index, long size) {
        Page<VideoServer> page = new Page<>(index, size);
        IPage<VideoServer> pageData = videoServerMapper.selectPage(page, example);
        List<VideoServer> list = pageData.getRecords();
        for (VideoServer server : list) {
            String serviceConfig = server.getServiceConfig();
            JSONObject config = JSONObject.parseObject(serviceConfig);
            server.setConfig(config);
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断视频服务ID是否存在
     * @param serverId 视频服务ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotServerId(String serverId) {
        QueryWrapper<VideoServer> example = new QueryWrapper<>();
        example.eq("server_id", serverId);
        return !videoServerMapper.exists(example);
    }

    /**
     * 根据视频服务ID查询视频服务信息
     * @param serverId 视频服务ID
     * @return 视频服务信息
     */
    public VideoServer find(@Param("serverId") String serverId) {
        VideoServer server = videoServerMapper.selectById(serverId);
        String serviceConfig = server.getServiceConfig();
        JSONObject config = JSONObject.parseObject(serviceConfig);
        server.setConfig(config);
        return server;
    }

    /**
     * 插入视频服务信息
     * @param record 视频服务信息
     * @throws Exception 异常信息
     */
    public void insert(VideoServer record) throws Exception {
        String serverId = InternalIdUtil.createId();
        record.setServerId(serverId);
        record.setStatus(0);
        record.setCreateTime(new Date());
        int ret = videoServerMapper.insert(record);
        if(ret < 1) {
            throw new Exception("新增视频服务失败");
        }
    }

    /**
     * 修改视频服务信息
     * @param record 视频服务信息
     */
    public VideoServer update(@Param("record") VideoServer record) {
        VideoServer server = videoServerMapper.selectById(record.getServerId());
        server.setServerName(record.getServerName());
        server.setServerType(record.getServerType());
        server.setServiceConfig(record.getServiceConfig());
        videoServerMapper.updateById(server);
        return server;
    }

    public VideoServer updateStatus(@Param("serverId") String serverId) {
        VideoServer server = videoServerMapper.selectById(serverId);
        int status = server.getStatus();
        if(status == 0) {
            server.setStatus(1);
        } else {
            server.setStatus(0);
        }
        videoServerMapper.updateById(server);
        return server;
    }

    /**
     * 删除视频服务信息
     * @param serverId 视频服务ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("serverId") String serverId) throws Exception {
        return videoServerMapper.deleteById(serverId);
    }

    /**
     * 批量删除视频服务信息
     * @param idList 视频服务ID列表
     * @return 删除数量
     */
    public int delArray(List<String> idList) {
        QueryWrapper<VideoServer> example = new QueryWrapper<>();
        example.in("server_id", idList);
        return videoServerMapper.delete(example);
    }
}
