package org.bem.iot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.mapper.postgresql.SystemNoticeMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.system.SystemNotice;
import org.bem.iot.model.user.UserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知公告
 * @author jakybland
 */
@Service
public class SystemNoticeService {
    @Resource
    SystemNoticeMapper systemNoticeMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    /**
     * 统计通知公告数量
     * @param example 查询条件
     * @return 通知公告数量
     */
    public long count(QueryWrapper<SystemNotice> example) {
        return systemNoticeMapper.selectCount(example);
    }

    /**
     * 获取指定通知公告列表
     * @param example 查询条件
     * @param size 数量
     * @return 通知公告列表
     */
    public List<SystemNotice> selectTop(QueryWrapper<SystemNotice> example, long size) {
        Page<SystemNotice> page = new Page<>(1L, size);
        IPage<SystemNotice> result = systemNoticeMapper.selectPage(page, example);
        List<SystemNotice> list = result.getRecords();
        for (SystemNotice notice : list) {
            int userId = notice.getUserId();
            UserInfo user = userInfoMapper.selectById(userId);
            Map<String, Object> userMap = new HashMap<>();
            if (user == null) {
                userMap.put("userName", "");
                userMap.put("nickName", "匿名");
                userMap.put("headImg", "/src/assets/images/schoolboy.png");
            } else {
                userMap.put("userName", user.getUserName());
                userMap.put("nickName", user.getNickName());
                userMap.put("headImg", user.getHeadImg());
            }
            notice.setUser(userMap);
        }
        return list;
    }

    /**
     * 分页查询通知公告列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 通知公告列表
     */
    public IPage<SystemNotice> selectPage(QueryWrapper<SystemNotice> example, long index, long size) {
        Page<SystemNotice> page = new Page<>(index, size);
        IPage<SystemNotice> result = systemNoticeMapper.selectPage(page, example);
        List<SystemNotice> list = result.getRecords();
        for (SystemNotice notice : list) {
            int userId = notice.getUserId();
            UserInfo user = userInfoMapper.selectById(userId);
            Map<String, Object> userMap = new HashMap<>();
            if (user == null) {
                userMap.put("userName", "");
                userMap.put("nickName", "匿名");
                userMap.put("headImg", "/src/assets/images/schoolboy.png");
            } else {
                userMap.put("userName", user.getUserName());
                userMap.put("nickName", user.getNickName());
                userMap.put("headImg", user.getHeadImg());
            }
            notice.setUser(userMap);
        }
        result.setRecords(list);
        return result;
    }

    /**
     * 判断指定通知公告是否不存在
     * @param noticeId id
     * @return 存在返回true，不存在返回false
     */
    public boolean existNotNotice(int noticeId) {
        QueryWrapper<SystemNotice> example = new QueryWrapper<>();
        example.eq("notice_id", noticeId);
        return !systemNoticeMapper.exists(example);
    }

    /**
     * 查询通知公告
     * @param noticeId 通知公告ID
     * @return 通知公告信息
     */
    public SystemNotice find(@Param("noticeId") int noticeId) {
        SystemNotice notice = systemNoticeMapper.selectById(noticeId);
        int userId = notice.getUserId();
        UserInfo user = userInfoMapper.selectById(userId);
        Map<String, Object> userMap = new HashMap<>();
        if (user == null) {
            userMap.put("userName", "");
            userMap.put("nickName", "匿名");
            userMap.put("headImg", "/src/assets/images/schoolboy.png");
        } else {
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("headImg", user.getHeadImg());
        }
        notice.setUser(userMap);
        return notice;
    }

    /**
     * 查询通知公告
     * @param noticeId 通知公告ID
     * @return 通知公告信息
     */
    public SystemNotice findMeta(@Param("noticeId") int noticeId) {
        return systemNoticeMapper.selectById(noticeId);
    }

    /**
     * 添加通知公告
     * @param record 通知公告信息
     * @throws Exception 异常信息
     */
    public void insert(SystemNotice record) throws Exception {
        record.setNoticeId(null);
        int ret = systemNoticeMapper.insert(record);
        if(ret <= 0) {
            throw new Exception("新增公告信息失败");
        }
    }

    /**
     * 修改通知公告
     * @param record 通知公告信息
     */
    public void update(@Param("record") SystemNotice record) {
        systemNoticeMapper.updateById(record);
    }

    /**
     * 删除通知公告
     * @param noticeId 通知公告ID
     * @return 删除数量
     */
    public int del(@Param("noticeId") int noticeId) {
        return systemNoticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除通知公告
     * @param idList 通知公告ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        return systemNoticeMapper.deleteBatchIds(idList);
    }
}
