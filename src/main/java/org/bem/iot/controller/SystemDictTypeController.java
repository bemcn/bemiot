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
import org.bem.iot.model.system.SystemDictType;
import org.bem.iot.model.system.SystemDictTypeVo;
import org.bem.iot.service.SystemDictTypeService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 字典类型
 * @author jakybland
 */
@RestController
@RequestMapping("/dictType")
public class SystemDictTypeController {
    @Resource
    SystemDictTypeService systemDictTypeService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取字典类型列表
     */
    @GetMapping("/getDictTypeList")
    @PublicHeadLimit
    @ResponseBody
    public void getDictTypeList() {
        JSONObject jsonObject;
        try {
            QueryWrapper<SystemDictType> example = new QueryWrapper<>();
            List<SystemDictTypeVo> list = systemDictTypeService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取字典类型分页列表
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDictTypePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDictTypePageList(@RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<SystemDictType> example = new QueryWrapper<>();
            IPage<SystemDictTypeVo> page = systemDictTypeService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取字典类型
     * @param id id
     */
    @GetMapping("/getDictType")
    @PublicHeadLimit
    @ResponseBody
    public void getDictType(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(systemDictTypeService.existsNotTypeId(id)) {
            jsonObject = ResponseUtil.getErrorJson("字典类型不存在");
        } else {
            SystemDictType data = systemDictTypeService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增字典类型
     * @param record 字典类型
     */
    @PostMapping("/addDictType")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDictType(@Validated(Add.class) @Valid SystemDictType record) {
        JSONObject jsonObject;
        try {
            systemDictTypeService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "字典类型", "新增", "新增字典类型，【名称】" + record.getTypeName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑字典类型
     * @param record 字典类型
     */
    @PostMapping("/editDictType")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDictType(@Validated(Edit.class) @Valid SystemDictType record) {
        JSONObject jsonObject;
        try {
            systemDictTypeService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "字典类型", "修改", "修改字典类型，【名称】" + record.getTypeName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除字典类型
     * @param id id
     */
    @GetMapping("/delDictType")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDictType(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!systemDictTypeService.existsNotTypeId(id)) {
                SystemDictType dictType = systemDictTypeService.find(id);
                String typeName = dictType.getTypeName();
                int count = systemDictTypeService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "字典类型", "删除", "删除字典类型，【名称】" + typeName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个字典类型
     * @param ids id集合
     */
    @GetMapping("/delDictTypes")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDictTypes(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = systemDictTypeService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "字典类型", "删除", "批量删除" + count + "条字典类型信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
