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
import org.bem.iot.model.general.Platform;
import org.bem.iot.service.PlatformService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 第3方平台接入
 * @author jakybland
 */
@RestController
@RequestMapping("/platform")
public class PlatformController {
    @Resource
    PlatformService platformService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取第3方平台接入列表
     * @param accessType 接入类型 1：运用接口 2：第3方登录 3：数据接口
     * @param key 关键字
     */
    @GetMapping("/getPlatformList")
    @PublicHeadLimit
    @ResponseBody
    public void getPlatformList(@RequestParam(name="accessType", defaultValue="0") int accessType,
                                @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Platform> example = createExample(accessType, key);
            List<Platform> list = platformService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取第3方平台接入分页列表
     * @param accessType 接入类型 1：运用接口 2：第3方登录 3：数据接口
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getPlatformPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getPlatformPageList(@RequestParam(name="accessType", defaultValue="0") int accessType,
                                    @RequestParam(name="key", defaultValue="") String key,
                                    @RequestParam(name="index", defaultValue = "1") Integer index,
                                    @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Platform> example = createExample(accessType, key);
            IPage<Platform> page = platformService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<Platform> createExample(int accessType, String key) {
        QueryWrapper<Platform> example = new QueryWrapper<>();
        if(accessType > 0) {
            example.eq("access_type", accessType);
        }
        if(!StrUtil.isEmpty(key)) {
            example.like("platform_name", key);
        }
        example.orderByDesc("platform_id");
        return example;
    }

    /**
     * 获取第3方平台接入
     * @param id id
     */
    @GetMapping("/getPlatform")
    @PublicHeadLimit
    @ResponseBody
    public void getPlatform(@RequestParam(name="id", defaultValue="0") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(platformService.existsNotPlatformId(id)) {
            jsonObject = ResponseUtil.getErrorJson("第3方平台接入信息不存在");
        } else {
            Platform data = platformService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增第3方平台接入
     * @param record 第3方平台接入
     */
    @PostMapping("/addPlatform")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addPlatform(@Validated(Add.class) @Valid Platform record) {
        JSONObject jsonObject;
        try {
            platformService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "第3方接入", "新增", "新增第3方平台接入，【名称】" + record.getPlatformName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑第3方平台接入
     * @param record 第3方平台接入
     */
    @PostMapping("/editPlatform")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editPlatform(@Validated(Edit.class) @Valid Platform record) {
        JSONObject jsonObject;
        try {
            platformService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "第3方接入", "修改", "修改第3方平台接入，【名称】" + record.getPlatformName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改状态
     * @param id id
     */
    @GetMapping("/editStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editStatus(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!platformService.existsNotPlatformId(id)) {
                Platform platform = platformService.find(id);
                if(platform.getIsSystem() == 1) {
                    String platformName = platform.getPlatformName();
                    String option = "启用";
                    if (platform.getStatus() == 1) {
                        option = "停用";
                    }
                    platformService.updateStatus(id);

                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "第3方接入", "修改", option + "第3方平台接入，【名称】" + platformName);
                }
                jsonObject = ResponseUtil.getSuccessJson();
            } else {
                jsonObject = ResponseUtil.getErrorJson("第3方平台接入信息不存在");
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除第3方平台接入
     * @param id id
     */
    @GetMapping("/delPlatform")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delPlatform(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!platformService.existsNotPlatformId(id)) {
                Platform platform = platformService.find(id);
                String platformName = platform.getPlatformName();
                int count = platformService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "第3方接入", "删除", "删除第3方平台接入，【名称】" + platformName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个第3方平台接入
     * @param ids id集合
     */
    @GetMapping("/delPlatforms")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delPlatforms(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = platformService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "第3方接入", "删除", "批量删除" + count + "条第3方平台接入信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
