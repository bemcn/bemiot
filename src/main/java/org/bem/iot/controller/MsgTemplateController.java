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
import org.bem.iot.model.general.MsgTemplate;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.MsgTemplateService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模板
 * @author jakybland
 */
@RestController
@RequestMapping("/msgTemplate")
public class MsgTemplateController {
    @Resource
    MsgTemplateService msgTemplateService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取消息模板列表
     * @param scopeApp 应用范围
     * @param filed 搜索字段
     * @param key 关键字
     */
    @GetMapping("/getMsgTemplateList")
    @PublicHeadLimit
    @ResponseBody
    public void getMsgTemplateList(@RequestParam(name="scopeApp", defaultValue="") String scopeApp,
                                   @RequestParam(name="filed", defaultValue="") String filed,
                                   @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(scopeApp)) {
                example.like("scope_app", scopeApp);
            }
            if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                if ("name".equals(filed)) {
                    example.like("title", key);
                } else {
                    example.like("identity", key);
                }
            }
            example.orderByDesc("create_time");
            List<MsgTemplate> list = msgTemplateService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取消息模板分页列表
     * @param scopeApp 应用范围
     * @param filed 搜索字段
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getMsgTemplatePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getMsgTemplatePageList(@RequestParam(name="scopeApp", defaultValue="") String scopeApp,
                                       @RequestParam(name="filed", defaultValue="") String filed,
                                       @RequestParam(name="key", defaultValue="") String key,
                                       @RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<MsgTemplate> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(scopeApp)) {
                example.like("scope_app", scopeApp);
            }
            if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
                if ("name".equals(filed)) {
                    example.like("title", key);
                } else {
                    example.like("identity", key);
                }
            }
            example.orderByDesc("create_time");
            IPage<MsgTemplate> page = msgTemplateService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取消息模板
     * @param id id
     */
    @GetMapping("/getMsgTemplate")
    @PublicHeadLimit
    @ResponseBody
    public void getMsgTemplate(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1L) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(msgTemplateService.existsNotTemplateId(id)) {
            jsonObject = ResponseUtil.getErrorJson("消息模板信息不存在");
        } else {
            MsgTemplate data = msgTemplateService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增消息模板
     * @param record 消息模板
     */
    @PostMapping("/addMsgTemplate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addMsgTemplate(@Validated(Add.class) @Valid MsgTemplate record) {
        JSONObject jsonObject;
        try {
            if(msgTemplateService.existsIdentity(record.getScopeApp(), record.getIdentity())) {
                jsonObject = ResponseUtil.getErrorJson("模板标识已被使用");
            } else {
                msgTemplateService.insert(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "消息模板", "新增", "新增消息模板，【标题】" + record.getTitle());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑消息模板
     * @param record 消息模板
     */
    @PostMapping("/editMsgTemplate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editMsgTemplate(@Validated(Edit.class) @Valid MsgTemplate record) {
        JSONObject jsonObject;
        try {
            if(msgTemplateService.existsIdentity(record.getTemplateId(), record.getScopeApp(), record.getIdentity())) {
                jsonObject = ResponseUtil.getErrorJson("模板标识已被使用");
            } else {
                msgTemplateService.update(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "消息模板", "修改", "修改消息模板，【标题】" + record.getTitle());

                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除消息模板
     * @param id id
     */
    @GetMapping("/delMsgTemplate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delMsgTemplate(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        try {
            if(!msgTemplateService.existsNotTemplateId(id)) {
                MsgTemplate msgTemplate = msgTemplateService.find(id);
                String title = msgTemplate.getTitle();
                int count = msgTemplateService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "消息模板", "删除", "删除消息模板，【标题】" + title);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个消息模板
     * @param ids id集合
     */
    @GetMapping("/delMsgTemplates")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delMsgTemplates(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
                int count = msgTemplateService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "消息模板", "删除", "批量删除" + count + "条消息模板信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
