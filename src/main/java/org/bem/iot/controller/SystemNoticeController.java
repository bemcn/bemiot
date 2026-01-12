package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.system.SystemNotice;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.SystemNoticeService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统公告
 * @author jaky
 */
@RestController
@RequestMapping("/notice")
public class SystemNoticeController {
    @Resource
    SystemNoticeService systemNoticeService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取通知公告分页列表
     * @param key 关键字
     * @param type 公告类型 1：通知 2：公告
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getNoticePageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getNoticePageList(@RequestParam(name="key", defaultValue="") String key,
                                  @RequestParam(name="type", defaultValue="0") int type,
                                  @RequestParam(name="startDate", defaultValue="") String startDate,
                                  @RequestParam(name="endDate", defaultValue="") String endDate,
                                  @RequestParam(name="index", defaultValue = "1") Integer index,
                                  @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<SystemNotice> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("notice_title", key);
            }
            if(type > 0) {
                example.eq("notice_type", type);
            }
            if(!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
                Date starTime;
                Date endTime;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    starTime = format.parse(startDate);
                    endTime = format.parse(endDate);
                    example.between("create_time", starTime, endTime);
                } catch (ParseException ignored) {
                }
            }
            example.orderByDesc("create_time");
            IPage<SystemNotice> page = systemNoticeService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取通知公告
     * @param id id
     */
    @GetMapping("/getNotice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getNotice(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(systemNoticeService.existNotNotice(id)) {
            jsonObject = ResponseUtil.getErrorJson("通知公告不存在");
        } else {
            SystemNotice data = systemNoticeService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增通知公告
     * @param record 通知公告
     */
    @PostMapping("/addNotice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addNotice(@Validated(Add.class) @Valid SystemNotice record) {
        JSONObject jsonObject;
        try {
            record.setCreateTime(new Date());
            systemNoticeService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "通知公告", "新增", "新增通知公告，【标题】" + record.getNoticeTitle());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑通知公告
     * @param record 通知公告
     */
    @PostMapping("/editNotice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editNotice(@Validated(Edit.class) @Valid SystemNotice record) {
        JSONObject jsonObject;
        try {
            SystemNotice notice = systemNoticeService.findMeta(record.getNoticeId());
            record.setCreateTime(notice.getCreateTime());
            systemNoticeService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "通知公告", "修改", "修改通知公告，【标题】" + record.getNoticeTitle());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除通知公告
     * @param id id
     */
    @GetMapping("/delNotice")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delNotice(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!systemNoticeService.existNotNotice(id)) {
                SystemNotice record = systemNoticeService.findMeta(id);
                int count = systemNoticeService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "通知公告", "删除", "删除通知公告，【标题】" + record.getNoticeTitle());
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个通知公告
     * @param ids id集合
     */
    @GetMapping("/delNotices")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delNotices(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
                int count = systemNoticeService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "通知公告", "删除", "批量删除" + count + "条通知公告");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
