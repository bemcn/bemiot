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
import org.bem.iot.model.scene.DataBridge;
import org.bem.iot.service.DataBridgeService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 数据桥接
 * @author your-name
 */
@RestController
@RequestMapping("/dataBridge")
public class DataBridgeController {
    @Resource
    DataBridgeService dataBridgeService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取数据桥接列表
     * @param key 关键字
     */
    @GetMapping("/getDataBridgeList")
    @PublicHeadLimit
    @ResponseBody
    public void getDataBridgeList(@RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DataBridge> example = createExample(key);
            List<DataBridge> list = dataBridgeService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取数据桥接分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getDataBridgePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getDataBridgePageList(@RequestParam(name="key", defaultValue="") String key,
                                      @RequestParam(name="index", defaultValue = "1") Integer index,
                                      @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<DataBridge> example = createExample(key);
            IPage<DataBridge> page = dataBridgeService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<DataBridge> createExample(String key) {
        QueryWrapper<DataBridge> example = new QueryWrapper<>();
        if(!StrUtil.isEmpty(key)) {
            example.like("bridge_name", key);
        }
        example.orderByDesc("bridge_id");
        return example;
    }

    /**
     * 获取数据桥接
     * @param id id
     */
    @GetMapping("/getDataBridge")
    @PublicHeadLimit
    @ResponseBody
    public void getDataBridge(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(dataBridgeService.existsNotBridgeId(id)) {
            jsonObject = ResponseUtil.getErrorJson("数据桥接信息不存在");
        } else {
            DataBridge data = dataBridgeService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增数据桥接
     * @param record 数据桥接
     */
    @PostMapping("/addDataBridge")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addDataBridge(@Validated(Add.class) @Valid DataBridge record) {
        JSONObject jsonObject;
        try {
            dataBridgeService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "数据桥接", "新增", "新增数据桥接，【名称】" + record.getBridgeName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑数据桥接
     * @param record 数据桥接
     */
    @PostMapping("/editDataBridge")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editDataBridge(@Validated(Edit.class) @Valid DataBridge record) {
        JSONObject jsonObject;
        try {
            dataBridgeService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "数据桥接", "修改", "修改数据桥接，【名称】" + record.getBridgeName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除数据桥接
     * @param id id
     */
    @GetMapping("/delDataBridge")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDataBridge(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!StrUtil.isEmpty(id) && !dataBridgeService.existsNotBridgeId(id)) {
                DataBridge dataBridge = dataBridgeService.find(id);
                String bridgeName = dataBridge.getBridgeName();
                int count = dataBridgeService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "数据桥接", "删除", "删除数据桥接，【名称】" + bridgeName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个数据桥接
     * @param ids id集合
     */
    @GetMapping("/delDataBridges")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delDataBridges(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.asList(ids.split(","));
                int count = dataBridgeService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "数据桥接", "删除", "批量删除" + count + "条数据桥接信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}