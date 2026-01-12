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
import org.bem.iot.model.scene.SceneLinkage;
import org.bem.iot.service.SceneLinkageService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 场景联动
 * @author jakybland
 */
@RestController
@RequestMapping("/sceneLinkage")
public class SceneLinkageController {
    @Resource
    SceneLinkageService sceneLinkageService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取场景联动信息列表
     * @param sceneGroupId 分组ID
     * @param key 关键字
     */
    @GetMapping("/getSceneLinkageList")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneLinkageList(@RequestParam(name="sceneGroupId", defaultValue="0") int sceneGroupId,
                                    @RequestParam(name="key", defaultValue="") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<SceneLinkage> example = createExample(sceneGroupId, key);
            List<SceneLinkage> list = sceneLinkageService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取场景联动信息分页列表
     * @param sceneGroupId 分组ID
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getSceneLinkagePageList")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneLinkagePageList(@RequestParam(name="sceneGroupId", defaultValue="0") int sceneGroupId,
                                        @RequestParam(name="key", defaultValue="") String key,
                                        @RequestParam(name="index", defaultValue = "1") Integer index,
                                        @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<SceneLinkage> example = createExample(sceneGroupId, key);
            IPage<SceneLinkage> page = sceneLinkageService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<SceneLinkage> createExample(int sceneGroupId, String key) {
        QueryWrapper<SceneLinkage> example = new QueryWrapper<>();
        if(sceneGroupId > 0) {
            example.eq("scene_group_id", sceneGroupId);
        }
        if(!StrUtil.isEmpty(key)) {
            example.like("scene_name", key);
        }
        example.orderByDesc("create_time");
        return example;
    }

    /**
     * 获取场景联动信息
     * @param id id
     */
    @GetMapping("/getSceneLinkage")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneLinkage(@RequestParam(name="id", defaultValue="0") long id) {
        JSONObject jsonObject;
        if(id < 1L) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(sceneLinkageService.existsNotSceneId(id)) {
            jsonObject = ResponseUtil.getErrorJson("场景联动不存在");
        } else {
            SceneLinkage data = sceneLinkageService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增场景联动信息
     * @param record 场景联动信息
     */
    @PostMapping("/addSceneLinkage")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addSceneLinkage(@Validated(Add.class) @Valid SceneLinkage record) {
        JSONObject jsonObject;
        try {
            sceneLinkageService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "场景联动", "新增", "新增场景联动信息，【名称】" + record.getSceneName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑场景联动信息
     * @param record 场景联动信息
     */
    @PostMapping("/editSceneLinkage")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editSceneLinkage(@Validated(Edit.class) @Valid SceneLinkage record) {
        JSONObject jsonObject;
        try {
            sceneLinkageService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "场景联动", "修改", "修改场景联动信息，【名称】" + record.getSceneName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改状态
     * @param id id
     * @param status 状态 0：停用 1：启用
     */
    @GetMapping("/updateStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateStatus(@RequestParam(name="id", defaultValue="0") long id, @RequestParam(name="status", defaultValue="0") int status) {
        JSONObject jsonObject;
        if(id < 1L) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(sceneLinkageService.existsNotSceneId(id)) {
            jsonObject = ResponseUtil.getErrorJson("场景联动不存在");
        } else {
            try {
                SceneLinkage sceneLinkage = sceneLinkageService.findMeta(id);
                String sceneName = sceneLinkage.getSceneName();
                sceneLinkageService.updateStatus(id, status);

                String option = "停用";
                if(status == 1) {
                    option = "启用";
                }

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "场景联动", "修改", option + "场景联动，【名称】" + sceneName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除场景联动信息
     * @param id id
     */
    @GetMapping("/delSceneLinkage")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSceneLinkage(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!sceneLinkageService.existsNotSceneId(id)) {
                SceneLinkage sceneLinkage = sceneLinkageService.findMeta(id);
                String sceneName = sceneLinkage.getSceneName();
                int count = sceneLinkageService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "场景联动", "删除", "删除场景联动信息，【名称】" + sceneName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个场景联动信息
     * @param ids id集合
     */
    @GetMapping("/delSceneLinkages")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSceneLinkages(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = sceneLinkageService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "场景联动", "删除", "批量删除" + count + "条场景联动");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
