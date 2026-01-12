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
import org.bem.iot.model.general.Protocols;
import org.bem.iot.service.ProtocolsService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 通讯协议
 * @author jakybland
 */
@RestController
@RequestMapping("/protocols")
public class ProtocolsController {
    @Resource
    ProtocolsService protocolsService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取通讯协议列表
     */
    @GetMapping("/getProtocolsList")
    @PublicHeadLimit
    @ResponseBody
    public void getProtocolsList() {
        JSONObject jsonObject;
        try {
            QueryWrapper<Protocols> example = new QueryWrapper<>();
            example.orderByAsc("protocol_id");
            List<Protocols> list = protocolsService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取通讯协议分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getProtocolsPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getProtocolsPageList(@RequestParam(name="key", defaultValue="") String key,
                                     @RequestParam(name="index", defaultValue = "1") Integer index,
                                     @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Protocols> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("protocol_name", key);
            }
            example.orderByDesc("protocol_id");
            IPage<Protocols> page = protocolsService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取通讯协议
     * @param id id
     */
    @GetMapping("/getProtocols")
    @PublicHeadLimit
    @ResponseBody
    public void getProtocols(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1L) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(protocolsService.existsNotProtocolsId(id)) {
            jsonObject = ResponseUtil.getErrorJson("通讯协议信息不存在");
        } else {
            Protocols data = protocolsService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增通讯协议
     * @param record 通讯协议
     */
    @PostMapping("/addProtocols")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addProtocols(@Validated(Add.class) @Valid Protocols record) {
        JSONObject jsonObject;
        try {
            protocolsService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "通讯协议", "新增", "新增通讯协议，【名称】" + record.getProtocolName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑通讯协议
     * @param record 通讯协议
     */
    @PostMapping("/editProtocols")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editProtocols(@Validated(Edit.class) @Valid Protocols record) {
        JSONObject jsonObject;
        try {
            protocolsService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "通讯协议", "修改", "修改通讯协议，【名称】" + record.getProtocolName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除通讯协议
     * @param id id
     */
    @GetMapping("/delProtocols")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProtocols(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!protocolsService.existsNotProtocolsId(id)) {
                Protocols protocols = protocolsService.find(id);
                String protocolName = protocols.getProtocolName();
                int count = protocolsService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "通讯协议", "删除", "删除通讯协议，【名称】" + protocolName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个通讯协议
     * @param ids id集合
     */
    @GetMapping("/delProtocolss")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delProtocolss(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = protocolsService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "通讯协议", "删除", "批量删除" + count + "条通讯协议信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
