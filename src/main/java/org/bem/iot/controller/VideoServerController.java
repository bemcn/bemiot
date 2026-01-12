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
import org.bem.iot.model.video.VideoServer;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.VideoServerService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 视频服务管理
 * @author jakybland
 */
@RestController
@RequestMapping("/video_server")
public class VideoServerController {
    @Resource
    VideoServerService videoServerService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取视频服务列表
     * @param key 关键字
     * @param status 状态 0：所有 1：停用 1：启用
     */
    @GetMapping("/getVideoServerList")
    @PublicHeadLimit
    @ResponseBody
    public void getVideoServerList(@RequestParam(name="key", defaultValue="") String key,
                                   @RequestParam(name="status", defaultValue="0") Integer status) {
        JSONObject jsonObject;
        try {
              //  35000000011320000101   172.150.1.3  FV123456
//            34020000 00 200 0000001
//            34020000 00 200 0000001
//            34020000 00 132 0000001
//            35011101 32 013 200000201
//            35000000012000000001  --服务器ID
//            3500000001 --服务器域
//                0.0.0.0 --服务器地址
            QueryWrapper<VideoServer> example = createExample(key, status);
            List<VideoServer> list = videoServerService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取视频服务分页列表
     * @param key 关键字
     * @param status 状态 0：所有 1：停用 1：启用
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getVideoServerPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getVideoServerPageList(@RequestParam(name="key", defaultValue="") String key,
                                       @RequestParam(name="status", defaultValue="0") Integer status,
                                       @RequestParam(name="index", defaultValue = "1") Integer index,
                                       @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<VideoServer> example = createExample(key, status);
            IPage<VideoServer> page = videoServerService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<VideoServer> createExample(String key, Integer status) {
        QueryWrapper<VideoServer> example = new QueryWrapper<>();
        if(!StrUtil.isEmpty(key)) {
            example.like("server_name", key);
        }
        if(status > 0) {
            if(status == 1) {
                example.eq("status", 0);
            } else {
                example.eq("status", 1);
            }
        }
        example.orderByDesc("create_time");
        return example;
    }

    /**
     * 获取视频服务
     * @param id id
     */
    @GetMapping("/getVideoServer")
    @PublicHeadLimit
    @ResponseBody
    public void getVideoServer(@RequestParam(name="id", defaultValue="0") String id) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(id)) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空");
        } else if(videoServerService.existsNotServerId(id)) {
            jsonObject = ResponseUtil.getErrorJson("视频配置不存在");
        } else {
            VideoServer data = videoServerService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增视频服务
     * @param record 视频服务
     */
    @PostMapping("/addVideoServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addVideoServer(@Validated(Add.class) @Valid VideoServer record) {
        JSONObject jsonObject;
        try {
            videoServerService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "视频配置", "新增", "新增视频服务，【名称】" + record.getServerName());
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑视频服务
     * @param record 视频服务
     */
    @PostMapping("/editVideoServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editVideoServer(@Validated(Edit.class) @Valid VideoServer record) {
        JSONObject jsonObject;
        try {
            videoServerService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "视频配置", "修改", "修改视频服务，【名称】" + record.getServerName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 变更状态
     * @param id id
     */
    @GetMapping("/updateStatus")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateStatus(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!videoServerService.existsNotServerId(id)) {
                VideoServer videoServer = videoServerService.updateStatus(id);
                String serverName = videoServer.getServerName();
                String statusName = "启用";
                if(videoServer.getStatus() == 0) {
                    statusName = "禁用";
                }

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "视频配置", "编辑", "变更视频服务为" + statusName + "状态，【名称】" + serverName);
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除视频服务
     * @param id id
     */
    @GetMapping("/delVideoServer")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delVideoServer(@RequestParam(name="id", defaultValue="") String id) {
        JSONObject jsonObject;
        try {
            if(!videoServerService.existsNotServerId(id)) {
                VideoServer videoServer = videoServerService.find(id);
                String serverName = videoServer.getServerName();
                int count = videoServerService.del(id);
                if (count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "视频配置", "删除", "删除视频服务，【名称】" + serverName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个视频服务
     * @param ids id集合
     */
    @GetMapping("/delVideoServers")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delVideoServers(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<String> idList = Arrays.stream(ids.split(",")).toList();
                int count = videoServerService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "视频配置", "删除", "批量删除" + count + "条视频配置");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
