package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.scene.SceneGroup;
import org.bem.iot.service.SceneGroupService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 场景分组
 * @author jakybland
 */
@RestController
@RequestMapping("/sceneGroup")
public class SceneGroupController {
    @Resource
    SceneGroupService sceneGroupService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取分组列表
     */
    @GetMapping("/getSceneGroupList")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneGroupList() {
        JSONObject jsonObject;
        try {
            List<SceneGroup> list = sceneGroupService.select();

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取分组分页列表
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getSceneGroupPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneGroupPageList(@RequestParam(name="index", defaultValue = "1") Integer index,
                                      @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            IPage<SceneGroup> page = sceneGroupService.selectPage(index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取分组
     * @param id id
     */
    @GetMapping("/getSceneGroup")
    @PublicHeadLimit
    @ResponseBody
    public void getSceneGroup(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(sceneGroupService.existsNotSceneGroupId(id)) {
            jsonObject = ResponseUtil.getErrorJson("场景分组不存在");
        } else {
            SceneGroup data = sceneGroupService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增分组
     * @param record 分组
     */
    @PostMapping("/addSceneGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addSceneGroup(@Validated(Add.class) @Valid SceneGroup record) {
        JSONObject jsonObject;
        try {
            sceneGroupService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "场景分组", "新增", "新增分组，【名称】" + record.getGroupName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑分组
     * @param record 分组
     */
    @PostMapping("/editSceneGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editSceneGroup(@Validated(Edit.class) @Valid SceneGroup record) {
        JSONObject jsonObject;
        try {
            sceneGroupService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "场景分组", "修改", "修改分组，【名称】" + record.getGroupName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改排序
     * @param id id
     * @param orderNum 排序值
     */
    @GetMapping("/updateOrderNumber")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNumber(@RequestParam(name="id", defaultValue="0") int id, @RequestParam(name="orderNum", defaultValue="0") int orderNum) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(sceneGroupService.existsNotSceneGroupId(id)) {
            jsonObject = ResponseUtil.getErrorJson("场景分组不存在");
        } else {
            try {
                SceneGroup sceneGroup = sceneGroupService.find(id);
                String groupName = sceneGroup.getGroupName();
                sceneGroupService.updateOrder(id, orderNum);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "场景分组", "修改", "修改分组排序为" + orderNum + "，【名称】" + groupName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除分组
     * @param id id
     */
    @GetMapping("/delSceneGroup")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSceneGroup(@RequestParam(name="id", defaultValue="") int id) {
        JSONObject jsonObject;
        try {
            if(!sceneGroupService.existsNotSceneGroupId(id)) {
                SceneGroup sceneGroup = sceneGroupService.find(id);
                String groupName = sceneGroup.getGroupName();
                int count = sceneGroupService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "场景分组", "删除", "删除分组，【名称】" + groupName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个分组
     * @param ids id集合
     */
    @GetMapping("/delSceneGroups")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delSceneGroups(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = sceneGroupService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "场景分组", "删除", "批量删除" + count + "条场景分组");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
