package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.SpacePosition;
import org.bem.iot.service.SpacePositionService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 空间位置
 * @author jakybland
 */
@RestController
@RequestMapping("/position")
public class SpacePositionController {
    @Resource
    SpacePositionService spacePositionService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取空间位置树
     */
    @GetMapping("/getPositionTree")
    @PublicHeadLimit
    @ResponseBody
    public void getPositionTree() {
        JSONObject jsonObject;
        try {
            JSONArray jsonArray = spacePositionService.selectTree();
            jsonObject = ResponseUtil.getSuccessJson(jsonArray);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取空间位置树型表
     */
    @GetMapping("/getPositionTreeTable")
    @PublicHeadLimit
    @ResponseBody
    public void getPositionTreeTable() {
        JSONObject jsonObject;
        try {
            JSONArray jsonArray = spacePositionService.selectTreeTable();
            jsonObject = ResponseUtil.getSuccessJson(jsonArray);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取空间位置
     * @param id id
     */
    @GetMapping("/getPosition")
    @PublicHeadLimit
    @ResponseBody
    public void getPosition(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(spacePositionService.existsNotSpaceId(id)) {
            jsonObject = ResponseUtil.getErrorJson("空间位置不存在");
        } else {
            SpacePosition data = spacePositionService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增空间位置
     * @param record 空间位置
     */
    @PostMapping("/addPosition")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addPosition(@Validated(Add.class) @Valid SpacePosition record) {
        JSONObject jsonObject;
        try {
            spacePositionService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "空间位置", "新增", "新增空间位置，【名称】" + record.getSpaceName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑空间位置
     * @param record 空间位置
     */
    @PostMapping("/editPosition")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editPosition(@Validated(Edit.class) @Valid SpacePosition record) {
        JSONObject jsonObject;
        try {
            spacePositionService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "空间位置", "修改", "修改空间位置，【名称】" + record.getSpaceName());

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
        } else if(spacePositionService.existsNotSpaceId(id)) {
            jsonObject = ResponseUtil.getErrorJson("空间位置不存在");
        } else {
            try {
                SpacePosition dictType = spacePositionService.find(id);
                String spaceName = dictType.getSpaceName();
                spacePositionService.updateOrder(id, orderNum);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "空间位置", "修改", "修改空间位置排序为" + orderNum + "，【名称】" + spaceName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除空间位置
     * @param id id
     */
    @GetMapping("/delPosition")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delPosition(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!spacePositionService.existsNotSpaceId(id)) {
                SpacePosition dictType = spacePositionService.find(id);
                String spaceName = dictType.getSpaceName();
                int count = spacePositionService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "空间位置", "删除", "删除空间位置，【名称】" + spaceName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个空间位置
     * @param ids id集合
     */
    @GetMapping("/delPositions")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delPositions(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = spacePositionService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "空间位置", "删除", "批量删除" + count + "条空间位置");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
