package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.system.SystemDict;
import org.bem.iot.service.SystemDictService;
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
 * 字典信息
 * @author jakybland
 */
@RestController
@RequestMapping("/dict")
public class SystemDictController {
    @Resource
    SystemDictService systemDictService;

    @Resource
    SystemDictTypeService systemDictTypeService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取字典信息列表
     * @param typeId 字典类型ID
     */
    @GetMapping("/getDictList")
    @PublicHeadLimit
    @ResponseBody
    public void getDictList(@RequestParam(name="typeId", defaultValue = "0") Integer typeId) {
        JSONObject jsonObject;
        if(systemDictTypeService.existsNotTypeId(typeId)) {
            jsonObject = ResponseUtil.getErrorJson("字典ID错误");
        } else {
            try {
                List<SystemDict> list = systemDictService.select(typeId);

                jsonObject = ResponseUtil.getSuccessJson(list);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取字典信息分页列表
     * @param typeId 字典类型ID
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDictPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDictPageList(@RequestParam(name="typeId", defaultValue = "0") Integer typeId,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        if(systemDictTypeService.existsNotTypeId(typeId)) {
            jsonObject = ResponseUtil.getErrorJson("字典ID错误");
        } else {
            try {
                IPage<SystemDict> page = systemDictService.selectPage(typeId, index, size);

                jsonObject = ResponseUtil.getSuccessJson(page);
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取字典信息
     * @param id id
     */
    @GetMapping("/getDict")
    @PublicHeadLimit
    @ResponseBody
    public void getDict(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(systemDictService.existsNotDictId(id)) {
            jsonObject = ResponseUtil.getErrorJson("字典信息不存在");
        } else {
            SystemDict data = systemDictService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增字典信息
     * @param record 字典信息
     */
    @PostMapping("/addDict")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDict(@Validated(Add.class) @Valid SystemDict record) {
        JSONObject jsonObject;
        try {
            systemDictService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "字典信息", "新增", "新增字典信息，【名称】" + record.getDictLabel());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑字典信息
     * @param record 字典信息
     */
    @PostMapping("/editDict")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDict(@Validated(Edit.class) @Valid SystemDict record) {
        JSONObject jsonObject;
        try {
            systemDictService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "字典信息", "修改", "修改字典信息，【名称】" + record.getDictLabel());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改排序值
     * @param id id
     * @param orderNum 排序值
     */
    @GetMapping("/updateOrderNum")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNum(@RequestParam(name="id", defaultValue="0") int id, @RequestParam(name="orderNum", defaultValue="0") int orderNum) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(systemDictService.existsNotDictId(id)) {
            jsonObject = ResponseUtil.getErrorJson("字典信息不存在");
        } else {
            try {
                SystemDict dictType = systemDictService.find(id);
                String dictLabel = dictType.getDictLabel();
                systemDictService.updateOrder(id, orderNum);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "字典信息", "修改", "修改字典排序为" + orderNum + "，【名称】" + dictLabel);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除字典信息
     * @param id id
     */
    @GetMapping("/delDict")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDict(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!systemDictService.existsNotDictId(id)) {
                SystemDict dictType = systemDictService.find(id);
                String dictLabel = dictType.getDictLabel();
                int count = systemDictService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "字典信息", "删除", "删除字典信息，【名称】" + dictLabel);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个字典信息
     * @param ids id集合
     */
    @GetMapping("/delDicts")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDicts(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = systemDictService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "字典信息", "删除", "批量删除" + count + "条字典信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
